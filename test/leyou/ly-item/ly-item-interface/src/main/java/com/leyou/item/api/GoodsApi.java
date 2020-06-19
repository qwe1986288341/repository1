package com.leyou.item.api;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public interface GoodsApi {
    //创建一个接口 具体实现为 根据spu的id查询spu的对象
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    @PostMapping("sku/{id}")
    public Sku selectSkuById(@PathVariable("id")Long id);
}
