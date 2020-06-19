package com.leyou.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.service.SpecGroupService;
import com.leyou.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("spec")
public class SpecGroupController {

    @Autowired
    private SpecGroupService sgs;

    @Autowired
    private SpecParamService sps;

    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> groups(@PathVariable Long cid){
        //判断参数是否正确
        if(cid==null){
            ResponseEntity.notFound().build();
        }
        List<SpecGroup> list = sgs.findSpecGroupByCid(cid);
        return ResponseEntity.ok(list);
    }


//    @RequestMapping("/group")
//    public ResponseEntity<Void> group(@RequestBody SpecGroup specGroup){
//        System.out.println(specGroup);
//        //先判断参数是否有值 如果有值则进入另外的操作 如果没有值则返回空指针异常
//        if(Objects.isNull(specGroup)) {
//            return ResponseEntity.notFound().build();
//        }else {
//            //取出cid的值
//            Long cid = specGroup.getId();
//            //判断cid是否有值 如果有值 则说明是修改  如果没有值则说明是添加
//            if (cid == null){
//                //走到这里则说明是添加
//                System.out.println("我去了添加页面");
//                sgs.addSpecGroupOne(specGroup);
//            }else {
//                //到这里说明符合条件 直接去后台修改数据
//                System.out.println("我去了修改页面");
//                sgs.updateSpecGroupById(specGroup);
//            }
//            return ResponseEntity.ok().build();
//        }
//
//
//    }
    //修改是put请求 如果是修改 则走该方法
    @PutMapping("/group")
    public ResponseEntity<Void> group(@RequestBody SpecGroup specGroup){
            if(specGroup==null){
                ResponseEntity.notFound().build();
            }
        sgs.updateSpecGroupById(specGroup);
        return ResponseEntity.ok().build();
    }

    //添加是个post请求 如果是post请求则走这个方法
    @PostMapping("/group")
    public ResponseEntity<Void> group1(@RequestBody SpecGroup specGroup){
        if(specGroup==null){
            ResponseEntity.notFound().build();
        }
        sgs.addSpecGroupOne(specGroup);
        return ResponseEntity.ok().build();
    }

    //删除
    @DeleteMapping("/group/{gid}")
    public ResponseEntity<Void> params(@PathVariable Long gid){
        //判断这个参数是否符合规范
        if(gid == null){
            //走到这里说明id没有值 则要删除id
            ResponseEntity.notFound().build();
        }
        //到这里则说明符合规范 删除
        sgs.paramsById(gid);
        return ResponseEntity.ok().build();
    }

    //查询商品参数
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> params(
           @RequestParam(value = "gid",required = false) Long gid,
           @RequestParam(value = "cid",required = false) Long cid,
           @RequestParam(value = "searching",required = false)Boolean searching,
           @RequestParam(value = "generic",required = false)Boolean generic
    ){
        List<SpecParam> list = sps.queryByGid(gid,cid,searching,generic);
        if(CollectionUtils.isEmpty(list)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }


    //添加商品信息
    @PostMapping("param")
    public ResponseEntity<Void> param(@RequestBody SpecParam specParam){
        //判断对象是否有值
        if(specParam == null){
            return ResponseEntity.notFound().build();
        }
        //走到这里则说明有值 直接去添加
        sps.addSpecParamByObject(specParam);
        return ResponseEntity.ok().build();
    }

    //修改商品信息
    @PutMapping("param")
    public ResponseEntity<Void> param1(@RequestBody SpecParam specParam){
        //判断对象是否有值
        if(specParam == null){
            return ResponseEntity.notFound().build();
        }
        //走到这里则说明有值 直接去添加
        sps.UpdateSpecParamByObject(specParam);
        return ResponseEntity.ok().build();
    }

    //删除用户信息
    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> param2(@PathVariable Long id){
        //判断对象是否有值
        if(id == null){
            return ResponseEntity.notFound().build();
        }
        //走到这里则说明有值 直接去添加
        sps.DeleteSpecParamByLongId(id);
        return ResponseEntity.ok().build();
    }


}
