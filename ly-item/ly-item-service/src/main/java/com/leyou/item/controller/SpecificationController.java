package com.leyou.item.controller;

import com.leyou.item.pojo.Specification;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService ;

    //根据id查询是否有该规格
    @GetMapping("{id}")
    public ResponseEntity<String> querySpecificationByCategoryId(@PathVariable("id")Long id){
        Specification spec = this.specificationService.queryById(id);
        if(spec == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spec.getSpecifications());
    }

    //新增规格
    @PostMapping
    public void saveSpecification(Specification specification){
        specificationService.saveSpecification(specification);
    }

    //修改规格
    @PutMapping
    public void updateSpecification(Specification specification){
        specificationService.updateSpecification(specification);
    }

    //删除规格
    @DeleteMapping("{categoryId}")
    public void deleteSpecification(@PathVariable("categoryId") Long categoryId){
        specificationService.deleteSpecification(categoryId);
    }
}
