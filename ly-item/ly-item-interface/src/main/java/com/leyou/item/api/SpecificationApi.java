package com.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("spec")
public interface SpecificationApi {

    @GetMapping("/{cid}")
    public String querySpecByCid(@RequestParam("cid") Long id,
                                 @RequestParam(value = "searching",required = false) Boolean searching);
    @GetMapping("/spu/{id}")
    public String querySpuDetailBySpuId(@RequestParam("id") Long id,
                                        @RequestParam(value = "searching",required = false) Boolean searching);
    @GetMapping("{id}")
    public ResponseEntity<String> querySpecificationByCategoryId(@PathVariable("id")Long id);
}
