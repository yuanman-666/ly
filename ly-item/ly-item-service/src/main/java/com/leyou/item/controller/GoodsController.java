package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService ;

    /**
     * 分页查询SPU
     * */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>>querySpuByPage(
            @RequestParam(value = "page" , defaultValue = "1") Integer page,
            @RequestParam(value = "rows" , defaultValue = "5") Integer rows,
            @RequestParam(value = "key" , required = false) String key){

        System.out.println(key);
        //分页
        PageResult<SpuBo> result = this.goodsService.querySpuByPageAndSort(page,rows,key);
        if(result == null || result.getItems().size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    //新增商品
    @PostMapping
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){

        try {
            this.goodsService.save(spuBo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //修改商品

    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable(value = "id")Long id){
        SpuDetail detail = this.goodsService.querySpuDetailById(id);
        if(detail == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(detail);
    }

    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        List<Sku> skus = this.goodsService.querySkuBySpuId(id);
        if(skus == null || skus.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(skus);
    }

    @PutMapping
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spu){
        try {
            this.goodsService.update(spu);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("sspu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id")Long id){
        Spu spu = this.goodsService.querySpuById(id);
        if (spu == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spu);
    }

    @GetMapping("goodsById")
    public SpuBo queryGoodsById(@RequestParam("id") Long id){
        return goodsService.queryGoodsById(id);
    };

    @GetMapping("skuId")
    public ResponseEntity<Sku> querySkuById(@RequestParam("id")Long id){
       Sku sku = this.goodsService.querySkuById(id);
       if(sku == null){
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
       return ResponseEntity.ok(sku);
    };
}
