package com.leyou.start.com.leyou.listener;

import com.leyou.start.com.leyou.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.logging.FileHandler;

@Component
public class ItemListener {

    @Autowired
    GoodsHtmlService goodsHtmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "com.jiyun.leyou",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}))
    public void saveAndEdit(Long id){
        //无论是保存还是修改 最终的操作都是生成一个新的静态页面
        goodsHtmlService.toHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "com.jiyun.leyou",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void delete(Long id){
        //删除的话 但是页面已经静态化  这个时候就要删除改文件
        File file = new File("D:\\nginx-1.14.0\\html\\"+id+".html");
       if(file.exists()){
            file.delete();
       }
        goodsHtmlService.toHtml(id);
    }
}
