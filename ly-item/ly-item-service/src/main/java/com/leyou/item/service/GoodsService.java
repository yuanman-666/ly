package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate ;

    public PageResult<SpuBo> querySpuByPageAndSort(Integer page, Integer rows, String key) {

        //查询spu
        //分页，最多允许100条
        PageHelper.startPage(page, Math.min(rows, 100));
        //创建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //是否模糊查询
        if (StringUtil.isNotEmpty(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        Page<Spu> pageInfo = (Page<Spu>) this.spuMapper.selectByExample(example);

        List<SpuBo> list = pageInfo.getResult().stream().map(spu -> {
            // 2、把spu变为 spuBo
            SpuBo spuBo = new SpuBo();
            // 属性拷贝
            BeanUtils.copyProperties(spu, spuBo);

            // 3、查询spu的商品分类名称,要查三级分类
            List<String> names = this.categoryService.queryNameByIds(
                    Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            // 将分类名称拼接后存入
            spuBo.setCname(StringUtils.join(names, '/'));

            // 4、查询spu的品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), list);

    }

    @Transactional
    public void save(SpuBo spu) {
        //保存spu
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        this.spuMapper.insert(spu);
        //保存spu详细
        spu.getSpuDetail().setSpuId(spu.getId());
        this.spuDetailMapper.insert(spu.getSpuDetail());
        //保存sku和库存信息

        //发送mq
        this.sendMessage(spu.getId(),"insert");

    }

    private void saveSkuAndStock(List<Sku> skus, Long spuId) {
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            //保存sku
            sku.setSpuId(spuId);
            //初始化时间
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insert(sku);
            //保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(Integer.parseInt(sku.getStock() + ""));
            this.stockMapper.insert(stock);
        }
    }

    public SpuDetail querySpuDetailById(Long id) {
        return this.spuDetailMapper.selectByPrimaryKey(id);
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku recrod = new Sku();
        recrod.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(recrod);
        for (Sku sku : skus) {
            sku.setStock((long) this.stockMapper.selectByPrimaryKey(sku.getId()).getStock());
        }
        return skus;
    }

    @Transactional
    public void update(SpuBo spu) {
        //查询以前sku
        List<Sku> skus = this.querySkuBySpuId(spu.getId());
        //如果以前存在，则删除
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            //删除以前生存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);
            //删除以前的sku
            Sku recrod = new Sku();
            recrod.setSpuId(spu.getId());
            this.skuMapper.delete(recrod);
        }
        //新增sku和库存
        saveSkuAndStock(spu.getSkus(), spu.getId());
        //更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        //更新spu详细
        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

        //发送mq
        this.sendMessage(spu.getId(),"update");
    }

    public SpuBo queryGoodsById(Long id) {
        Sku sku = skuMapper.selectByPrimaryKey(id);

        Spu spu = this.spuMapper.selectByPrimaryKey(id);

        SpuDetail spuDetail = this.spuDetailMapper.selectByPrimaryKey(id);

        SpuBo spuBo = new SpuBo();
        spuBo.setId(spu.getId());
        spuBo.setBrandId(spu.getBrandId());
        spuBo.setCid1(spu.getCid1());
        spuBo.setCid2(spu.getCid2());
        spuBo.setCid3(spu.getCid3());
        spuBo.setTitle(spu.getTitle());
        spuBo.setSubTitle(spu.getSubTitle());
        spuBo.setSaleable(spu.getSaleable());
        spuBo.setValid(spu.getValid());
        spuBo.setCreateTime(spu.getCreateTime());
        spuBo.setLastUpdateTime(spu.getLastUpdateTime());
        List<Sku> list = new ArrayList<>();
        list.add(sku);
        spuBo.setSkus(list);
        spuBo.setSpuDetail(spuDetail);
        return spuBo;
    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    public void sendMessage(Long id ,String type){
        try {
            this.amqpTemplate.convertAndSend("item."+type,id);
        }catch (Exception e){
            System.out.println("发生错误");
        }
    }

    public Sku querySkuById(Long id) {
        return skuMapper.selectByPrimaryKey(id);
    }
}
