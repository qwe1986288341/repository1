package com.leyou.start.com.leyou.controller;

import com.leyou.start.com.leyou.service.GoodsHtmlService;
import com.leyou.start.com.leyou.service.GoodsService;
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
    GoodsService goodsService;

    @Autowired
    GoodsHtmlService goodsHtmlService;

    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){
            Map<String,Object> map = goodsService.buildGoods(id);
        model.addAllAttributes(map);
        goodsHtmlService.toHtml(id);

            return "item";
    }
}
