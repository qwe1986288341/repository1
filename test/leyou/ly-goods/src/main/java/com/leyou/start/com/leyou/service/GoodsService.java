package com.leyou.start.com.leyou.service;

import com.leyou.item.pojo.*;
import com.leyou.start.com.leyou.feign.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.util.*;

@Service
public class GoodsService {
    @Autowired
    BrandClient brandClient;
    @Autowired
    CategoryClient categoryClient;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    SkuClient skuClient;
    @Autowired
    SpeClient speClient;
    @Autowired
    SpuClient spuClient;
    public Map<String, Object> buildGoods(Long id) {
        Map<String,Object> map = new HashMap<>();
        //根据SpuId去查找spu
        Spu spu = goodsClient.querySpuById(id);
        //根据spuid去查找spuDetail
        SpuDetail spuDetail = spuClient.list(id);
        //根据spuid去查找sku
        List<Sku> skus = skuClient.list(id);
        //根据三个分类id查找出所属于的分类
        List<Long> ids = new ArrayList<>();
        ids.add(spu.getCid1());
        ids.add(spu.getCid2());
        ids.add(spu.getCid3());
        List<String> categorys = categoryClient.getCategorys(ids);
       List<Map<String,Object>> categories = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            Map<String,Object> map1 = new HashMap<>();
            map1.put("id",ids.get(i));
            map1.put("name",categorys.get(i));
            categories.add(map1);
        }

        //根据品牌id查找品牌的信息
        Long brandId = spu.getBrandId();
        Brand brandByBrandId = brandClient.getBrandByBrandId(brandId);

        //根据cid3查询出所有对应组下的规格参数
        List<SpecGroup> groups = speClient.groups(spu.getCid3());

        //sku中的显示  用于渲染选择项的标题
        List<SpecParam> params = speClient.params(null, spu.getCid3(), null, false);
        Map<Long,String > paramMap = new HashMap<>();
        params.forEach(param->{
            paramMap.put(param.getId(),param.getName());
        });
        // 封装spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);
        // 封装sku集合
        map.put("skus", skus);
        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brandByBrandId);
        // 规格参数组
        map.put("groups", groups);
        // 查询特殊规格参数
        map.put("params", paramMap);
        return map;
    }
}
