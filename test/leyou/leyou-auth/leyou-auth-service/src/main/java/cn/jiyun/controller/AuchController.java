package cn.jiyun.controller;

import cn.jiyun.common.pojo.utils.CookieUtils;
import cn.jiyun.entity.UserInfo;
import cn.jiyun.properties.JwtProperties;
import cn.jiyun.service.AuthService;
import cn.jiyun.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)//开启配置 从xml文件中获取信息
public class AuchController {

    @Autowired
    AuthService authService;

    @Autowired
    JwtProperties jwtProperties;

    //经过zuul网关
    //在这里写我们的业务逻辑
    //得到用户名密码 远程调用我们user服务的接口进行验证
    //jwt凭证一般选择存放在cookie中 cookie是有实效性的
    @PostMapping("valid")
    public ResponseEntity<Void> valid(String username,String password , HttpServletRequest request, HttpServletResponse response){
        //登录成功之后获取一个token 判断token是否为空 如果为空则返回null 如果不为空则存入到cookie中
        String token =  authService.valid(username,password);
        if(token!=null){
            //Cookie的操作
            CookieUtils.setCookie(request,response,"LY_TOKEN",token,jwtProperties.getExpire()*60);
           return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.badRequest().build();
        }

    }

    //验证是否已经登录
    //那就去查看token是否存在  并且token是否对的上
    //而token是存在cookie中的 所以我们就得到cookie
    //@CookieValue注解可以直接从请求中通过cookie中的key来获取他的值
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN")String token, HttpServletRequest request, HttpServletResponse response){
        //得到cookie中 就通过使用JWTUtil来验证tooken是否正确
        try{
            //token是token的值 jwtProperties.getPublicKey()是获取公钥文件
            //就是用来用公钥文件获取对应的token 来比对是否相等 从而判断是否登录
            //因为登录的时候 jwd和非对称加密才会生成token
            /**
             *  //问题？没发一次请求我们都会请求一次cookie来获取 所以cookie就会存活
             *             //如果用户长时间停留在某一个页面 某一个请求 长时间没有刷新的话 我们就重新刷新一次cookie
             *             //我们就让cookie死掉
             *             //如果用户一直再页面上玩耍使用 每过30分钟就刷新一下 则会导致用户体验贼差
             *             //1.将token延长到30分钟  那么我们就重新创建tookie覆盖之前的tooki
             * */
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //1.userInfo是否存在 如果存在就重新生成tookie并放在cookie中
            if(userInfo!=null){
                //获取到新的tken
                String newtoken = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
                //生成Cookie存放到其中
                CookieUtils.setCookie(request,response,"LY_TOKEN",newtoken,jwtProperties.getExpire()*60);
                return ResponseEntity.ok(userInfo);
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}
