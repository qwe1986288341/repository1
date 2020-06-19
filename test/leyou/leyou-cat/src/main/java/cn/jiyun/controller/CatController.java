package cn.jiyun.controller;

import cn.jiyun.pojo.Cart;
import cn.jiyun.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Api("购物车接口相关")
public class CatController {

    @Autowired
    CartService cartService;


   @PostMapping()
   @ApiOperation(value = "添加购物车的接口",notes = "添加购物车")
   @ApiImplicitParam(name = "cart",required = true,value = "要添加购物车的对象信息")
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
       //获取数据
       //去后台将数据添加到指定的数据库中 Redis
       cartService.addCart(cart);
       return ResponseEntity.ok().build();
   }

   //查询购物车
    @GetMapping("query")
    @ApiOperation(value = "查询购物车的接口",notes = "查询所有购物车")
    public ResponseEntity<List<Cart>> queryCart(){
       List<Cart> carts = cartService.queryCart();
        return ResponseEntity.ok(carts);
    }

    @PutMapping
    @ApiOperation(value = "修改购物车的接口",notes = "修改购物车")
    @ApiImplicitParam(name = "cart",required = true,value = "要修改购物车的对象信息")
    public ResponseEntity<Void> putCart(@RequestBody Cart cart){
        cartService.putCarts(cart);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{skuId}")
    @ApiOperation(value = "删除购物车的接口",notes = "删除购物车")
    @ApiImplicitParam(name = "skuId",required = true,value = "要删除购物车信息的skuId")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId")Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("addCarts")
//    public ResponseEntity<Void> addCarts(@RequestBody List<Cart> carts){
//        if (CollectionUtils.isEmpty(carts)){
//            return ResponseEntity.badRequest().build();
//        }
//        carts.forEach(cart -> {
//            System.out.println(cart);
//            cartService.addCart(cart);
//        });
//        return ResponseEntity.ok().build();
//    }
}
