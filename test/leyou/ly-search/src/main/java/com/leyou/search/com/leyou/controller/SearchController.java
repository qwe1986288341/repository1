package com.leyou.search.com.leyou.controller;

import cn.jiyun.common.pojo.PageResult;
import com.leyou.search.com.leyou.pojo.Goods;
import com.leyou.search.com.leyou.pojo.SearchRequest;
import com.leyou.search.com.leyou.pojo.SearchResult;
import com.leyou.search.com.leyou.service.GoodsSeravice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    GoodsSeravice goodsSeravice;

    @RequestMapping("page")
    public ResponseEntity<SearchResult> searchGoods(@RequestBody SearchRequest searchRequest){
        System.out.println(searchRequest.getFilter());
        SearchResult goodsList = goodsSeravice.SearchGoods(searchRequest);

        if(goodsList==null){
            return ResponseEntity.notFound().build();
        }
        List<Goods> items = goodsList.getItems();
        System.out.println(goodsList.getTotal());
        return ResponseEntity.ok(goodsList);
    }

}
