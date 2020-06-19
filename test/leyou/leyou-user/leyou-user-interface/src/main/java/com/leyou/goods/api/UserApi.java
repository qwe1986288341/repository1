package com.leyou.goods.api;

import com.leyou.goods.pojo.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface UserApi {
    @PostMapping("login")
    public User queryUser(@RequestParam("username") String username, @RequestParam("password") String password);
}
