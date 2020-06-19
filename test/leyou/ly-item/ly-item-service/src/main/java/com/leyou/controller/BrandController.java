package com.leyou.controller;

import cn.jiyun.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.service.BrandService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService bs;

    @RequestMapping("page")
    public ResponseEntity<PageResult<Brand>> page(
            @RequestParam(value = "key", required = false)String key,   //搜索条件
            @RequestParam(value = "page", defaultValue = "1")Integer page,  //当前页
            @RequestParam(value = "rows", defaultValue = "5")Integer rows,  //每页数据量
            @RequestParam(value = "sortBy", required = false)String sortBy, //排序字段
            @RequestParam(value = "desc", required = false)Boolean desc     //排序类型

    ){
        PageResult<Brand> pr = bs.queryByManycase(key,page,rows,sortBy,desc);
        //判断出最后的结果是否为空 如果为空则返回一个空指针404的错误  如果不为空则向下走表示正确
        if (pr.getItems()==null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pr);
    }

    @PostMapping
    public ResponseEntity<Void> add(Brand brand, @RequestParam(name = "cids") List<Integer> cids){
               bs.addBread(brand,cids);
               return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> add1(Brand brand, @RequestParam(name = "cids") List<Integer> cids){
            bs.updateBrand(brand,cids);
            return ResponseEntity.ok().build();
    }

    @RequestMapping("deleteById")
    public ResponseEntity<Void> deleteById(Integer id){
        bs.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cid/{id}")
    public ResponseEntity<List<Brand>> cid(@PathVariable("id") Integer id){
       List<Brand> brand =  bs.selectById(id);
        return ResponseEntity.ok(brand);
    }

    @GetMapping("{brandId}")
    public ResponseEntity<Brand> getBrandByBrandId(@PathVariable("brandId") Long id){
        Brand brand = bs.getBrandNameByBrandId(id);
        return ResponseEntity.ok(brand);
    }


}
