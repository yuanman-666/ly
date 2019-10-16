package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("brand")
public interface BrandApi {

    //根据品牌Id结合，查询品牌信息
    @GetMapping("/list")
    List<Brand> queryBrandByIds(@RequestParam("ids") List<Long> ids);
}
