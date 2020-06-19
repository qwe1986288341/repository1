package com.leyou.controller;

import com.leyou.goods.pojo.User;
import com.leyou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    //该方法用来验证用户名或者手机是否存在
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean bool = userService.findDate(data,type);
        if(bool == null){
            //400 请求内容有无 请求错误
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

    //send方法 用来处理短信验证码 生成验证码等等
    @PostMapping("send")
    public ResponseEntity<Void> send(String phone){
        userService.send(phone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, String code){
        Boolean register = userService.register(user, code);
        if(!register){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<User> queryUser(@RequestParam("username") String username,@RequestParam("password") String password){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        User u = userService.queryUser(user);
        if(u == null){
            //这里则说明登录失败 账号密码错误
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(u);
    }
}
