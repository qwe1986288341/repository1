package com.leyou.start.com.leyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {

     @Autowired
     GoodsService goodsService;
     @Autowired
     TemplateEngine templateEngine;

    public void toHtml(Long spuId){
        Map<String, Object> map = goodsService.buildGoods(spuId);
        //准备将数据放入到Context上下文对象中
        Context context = new Context();
        context.setVariables(map);
        PrintWriter printWriter = null;
        try{
            //准备输出流
            //利用nginx作为静态资源代理服务器
             printWriter = new PrintWriter("D:\\nginx-1.14.0\\html\\"+spuId+".html");
             templateEngine.process("item",context,printWriter);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            printWriter.close();
        }

    }
}
