package com.leyou.service;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Stock;
import com.leyou.mapper.SkuMapper;
import com.leyou.mapper.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SkuService {

    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    public List<Sku> selectSkuBySpuId(Long id) {
        Sku sku1 = new Sku();
        sku1.setSpuId(id);
       List<Sku> list = skuMapper.select(sku1);
       return list;
    }


    public Sku findSkuById(Integer id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    public Sku selectSkuById(Long id) {
        Sku sku = skuMapper.selectByPrimaryKey(id);
        return sku;
    }
}
