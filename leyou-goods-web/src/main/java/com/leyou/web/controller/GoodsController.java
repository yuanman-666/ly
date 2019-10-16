package com.leyou.web.controller;

import com.leyou.web.service.GoodsHtmlService;
import com.leyou.web.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("item")
public class GoodsController {

    @Autowired
    private GoodsService goodsService ;

    @Autowired
    private GoodsHtmlService goodsHtmlService ;

    @GetMapping("{id}.thml")
    public String toItemPage(Model model, @PathVariable("id")Long id){
        Map<String,Object> modelMap = this.goodsService.loadModel(id);
        //放入模型
        model.addAllAttributes(modelMap);
        // 页面静态化，可以使用异步线程来优化
        goodsHtmlService.createHtml(id);
        return "item";
    }
}
