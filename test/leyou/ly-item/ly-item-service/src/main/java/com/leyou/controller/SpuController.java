package com.leyou.controller;

import cn.jiyun.common.pojo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.SpuVo;
import com.leyou.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spu")
public class SpuController {

    @Autowired
    private SpuService spuService;

    @GetMapping("page")
    public ResponseEntity<PageResult<SpuVo>> querySpuBoByPage(
            @RequestParam(value = "key", required = false)String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows
    ){
        PageResult<SpuVo> pageResult = this.spuService.querySpuBoByPage(key, saleable, page, rows);
        if(CollectionUtils.isEmpty(pageResult.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<SpuDetail> list(@PathVariable("id") Long id){
        if(id == null){
            ResponseEntity.notFound().build();
        }
        SpuDetail spuVo =  spuService.selectById(id);
        return ResponseEntity.ok(spuVo);
    }




}
