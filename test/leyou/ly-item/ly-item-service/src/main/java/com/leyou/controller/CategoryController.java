package com.leyou.controller;

import com.leyou.item.pojo.Category;
import com.leyou.service.CategoryService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    CategoryService cs;

    @GetMapping("list")
    public ResponseEntity<List<Category>> list(@RequestParam(defaultValue = "0",name = "pid")Long pid){
       try {
           //判断该参数是否为空或者小于0  如果是的话则不符合条件 会爆出异常 则返回异常
           if (pid==null || pid.longValue() <0) {
               //说明参数不符合条件
               ResponseEntity.badRequest().build();
           }
           // 通过通用Mapper 根据id查找出来数据
           List<Category> categories = cs.findList(pid);
           //这里CollectionUtils是一个集合工具类   其中的isEmpty方法是用于判断该集合是否为null或者为0  如果是的话则返回一个错误 告诉前台参数传递错误
           if(CollectionUtils.isEmpty(categories)){
               ResponseEntity.notFound().build();
           }

           //如果都通过了上面的判断则说明该查询的条件符合我们的要求 和数据 我们就放行就可以
           return ResponseEntity.ok(categories);
       }catch (Exception e){
           e.printStackTrace();
       }

       //如果都没有的话就只能说明传递参数有问题 那么就返回一个null
        return null;
    }

    @RequestMapping("bid/{id}")
    private ResponseEntity<List<Category>> bid(@PathVariable("id") Integer id){
       if(id!=null) {
           List<Category> categories = cs.selectAll(id);
           if(!CollectionUtils.isEmpty(categories)){
               return ResponseEntity.ok(categories);
           }else{
               return ResponseEntity.notFound().build();
           }

       }
       return null;
    }

    //根据cid去查询品牌名称
    @GetMapping("names")
    public ResponseEntity<List<String>> getCategorys(@RequestParam List<Long> ids){
        //去后台查询
        List<String> names = cs.findCategorysByCid(ids);
        if (CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
}
