package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper ;

    public PageResult<Brand> queryBrandPageAndSort(Integer page,Integer rows,String sortBy,Boolean desc,String key){
        PageHelper.startPage(page,rows);
        Example example = new Example(Brand.class); //过滤
        if(key != null && !"".equals(key)){
            example.createCriteria().andLike("name","%"+key+"%")
                    .orEqualTo("letter");
        }
        if(sortBy != null && !"".equals(sortBy)){
            //排序
            String orderByClaues = sortBy + (desc ? " DESC " : " ASC ");
            example.setOrderByClause(orderByClaues);
        }

        //查询
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);

        return new PageResult<>(pageInfo.getTotal(),pageInfo);
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌信息
        this.brandMapper.insertSelective(brand);
        //新增品牌个分类中间表
        for (Long cid : cids) {
            this.brandMapper.insertCategoryBrand(cid,brand.getId());
        }
    }


    public void deleteCategoryById(Long bid) {
        //删除品牌类型
        this.brandMapper.deleteCategoryById(bid);
    }

    public void updateBrand(Brand brand, List<Long> cids) {
        this.brandMapper.updateByPrimaryKey(brand);
        for (Long cid: cids) {
            this.brandMapper.insertCategoryBrand(cid,brand.getId());
        }
    }

    public List<Brand> queryBrandByCategory(Long cid) {
        return this.brandMapper.queryByCategoryId(cid);
    }

    /**
     * 根据品牌id集合查询品牌信息
     * @param ids
     * @return
     */
    public List<Brand> queryBrandByBrandIds(List<Long> ids) {
        return this.brandMapper.selectByIdList(ids);
    }
}
