package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService ;

    //分页查询
    @RequestMapping(value = "page",method = RequestMethod.GET)
    public ResponseEntity<PageResult<Brand>> queryBrandPageAndSort(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            @RequestParam(value = "key",required = false) String key){

        PageResult<Brand> result = brandService.queryBrandPageAndSort(page,rows,sortBy,desc,key);
        if(result == null || result.getItems().size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    //新增品牌
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand ,@RequestParam("categories") List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //删除品牌类型
    @DeleteMapping("cid_bid/{bid}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("bid") Long bid){
        this.brandService.deleteCategoryById(bid);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //修改品牌
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand , @RequestParam("categories") List<Long> cids){
        this.brandService.updateBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //根据分类查询品牌
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>>queryBrandByCategory(@PathVariable(value = "cid")Long cid){
        List<Brand> list = this.brandService.queryBrandByCategory(cid);
        if(list == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> list = this.brandService.queryBrandByBrandIds(ids);
        if (list == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(list);
    }


}
