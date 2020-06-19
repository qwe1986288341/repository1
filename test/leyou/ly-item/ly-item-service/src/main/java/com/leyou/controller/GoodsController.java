package com.leyou.controller;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuVo;
import com.leyou.service.GoodsService;
import com.leyou.service.SkuService;
import com.leyou.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SpuService spuService;

    @PostMapping("/goods")
    private ResponseEntity<Void> addGoods(@RequestBody SpuVo spuVo){

        List<Sku> skus = spuVo.getSkus();
        goodsService.add(spuVo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/goods")
    private ResponseEntity<Void> updateGoods(@RequestBody SpuVo spuVo){

        //去修改
        goodsService.updateGoods(spuVo);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> list(Long id){
        List<Sku> list = skuService.selectSkuBySpuId(id);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/goods/deleteById")
    public ResponseEntity<Void> deleteById(Long id){
        if(id==null){
            ResponseEntity.notFound().build();
        }
            goodsService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/goods/saleable")
    public ResponseEntity<Void> saleable(Long id){
        if(id==null){
            ResponseEntity.notFound().build();
        }
        goodsService.saleable(id);
        return ResponseEntity.ok().build();
    }

    //创建一个接口 具体实现为  根据spu的id查询spu对象
    //创建一个接口 具体实现为 根据spu的id查询spu的对象
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spuList  =spuService.querySpuById(id);
        return ResponseEntity.ok(spuList);
    }

    //根据skuid查询出对应的sku对象
    @PostMapping("sku/{id}")
    public ResponseEntity<Sku> selectSkuById(@PathVariable("id")Long id){
        Sku sku = skuService.selectSkuById(id);
        return ResponseEntity.ok(sku);
    }

}
