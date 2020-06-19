package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("brand")
public interface BrandApi {
    @GetMapping("{brandId}")
    public Brand getBrandByBrandId(@PathVariable("brandId") Long id);
}
