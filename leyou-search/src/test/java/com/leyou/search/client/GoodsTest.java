package com.leyou.search.client;

import com.leyou.LySearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.SpuBo;
import com.leyou.search.good.SearchService;
import com.leyou.search.mapper.GoodsRepository;
import com.leyou.search.pojo.Goods;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class GoodsTest {

    @Autowired
    private GoodsClient goodsClient ;

    @Autowired
    private SearchService searchService ;

    @Autowired
    private GoodsRepository goodsRepository ;
    @Test
    public void load1(){
        loadData(1,180);

    }
    public void loadData(Integer pages,Integer rows){
        // 查询分页数据
        PageResult<SpuBo> result = this.goodsClient.querySpuByPage(pages, rows, null);
        List<SpuBo> spus = result.getItems();
        // 创建Goods集合
        List<Goods> goodsList = new ArrayList<>();
        // 遍历spu
        for (SpuBo spu : spus) {
            try {
                Goods goods = this.searchService.buildGoods(spu);
                goodsList.add(goods);
            } catch (Exception e) {
                break;
            }
        }
        this.goodsRepository.saveAll(goodsList);

    }
}
