package com.leyou.item.service;

import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper ;

    public Specification queryById(Long id){
        return this.specificationMapper.selectByPrimaryKey(id);
    }

    public void saveSpecification(Specification specification) {
        specificationMapper.insert(specification);
    }

    public void updateSpecification(Specification specification) {
        specificationMapper.updateByPrimaryKey(specification);
    }

    public void deleteSpecification(Long categoryId) {
        specificationMapper.deleteByPrimaryKey(categoryId);
    }
}
