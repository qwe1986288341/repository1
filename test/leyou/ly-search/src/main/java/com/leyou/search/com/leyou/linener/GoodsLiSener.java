package com.leyou.search.com.leyou.linener;

import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuVo;
import com.leyou.search.com.leyou.feign.GoodsClient;
import com.leyou.search.com.leyou.mapper.GoodsRepository;
import com.leyou.search.com.leyou.pojo.Goods;
import com.leyou.search.com.leyou.service.GoodsSeravice;
import org.elasticsearch.search.SearchService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsLiSener {

    @Autowired
    AmqpTemplate amqpTemplate;
    @Autowired
    GoodsSeravice goodsSeravice;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    GoodsRepository goodsRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.index.queue2", durable = "true"),
            exchange = @Exchange(
                    value = "com.jiyun.leyou",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}))
    public void saveAndEdit(Long id) throws Exception {
        //无论是保存还是修改 最终的操作都是生成一个新的静态页面
        if(id==null){
            return;
        }
        //先根据id 通过goodsClind服务 查找出 改spuid对应的spu
        Spu spu = goodsClient.querySpuById(id);
        System.out.println(spu+"我是Spu对象");
        SpuVo spuVo = new SpuVo();

        //这里就可以通过spu对象 调用service中的构建商品信息的方法buildGoods(spu)
        Goods goods = goodsSeravice.buildGoods(spu);
        goodsRepository.save(goods);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "com.jiyun.leyou",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void deleteGoodsElasticaserch(Long id) throws Exception {
        //无论是保存还是修改 最终的操作都是生成一个新的静态页面
        if(id==null){
            return;
        }
       goodsRepository.deleteById(id);
    }
}
