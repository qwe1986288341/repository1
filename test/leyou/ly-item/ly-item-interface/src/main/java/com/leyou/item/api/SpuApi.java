package com.leyou.item.api;

import cn.jiyun.common.pojo.PageResult;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.SpuVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("spu")
public interface SpuApi {
    @GetMapping("page")
    public PageResult<SpuVo> querySpuBoByPage(
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows
    );

    @GetMapping("/detail/{id}")
    public  SpuDetail list(@PathVariable("id") Long id);
}
