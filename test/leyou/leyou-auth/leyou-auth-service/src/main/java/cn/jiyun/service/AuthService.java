package cn.jiyun.service;

import cn.jiyun.properties.JwtProperties;
import cn.jiyun.entity.UserInfo;
import cn.jiyun.feign.UserClient;
import cn.jiyun.utils.JwtUtils;
import com.leyou.goods.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)//开启配置 从xml文件中获取信息
public class AuthService {
    @Autowired
    UserClient userClient;
    @Autowired
    JwtProperties jwtProperties;
    public String valid(String username, String password){
        //创建user对象 用来存储账号和密码
        User user = new User();
        //设置账号和密码
        user.setUsername(username);
        user.setPassword(password);
        //调用user服务 去查找方法
        User user1 = userClient.queryUser(username,password);
        System.out.println(user1);
        //判断返回来有没有值
        if(user1 == null){
            //没有就返回null
            return null;
        }else{
            //ras加密 jwt生成
            UserInfo userInfo =  new UserInfo();
            userInfo.setId(user1.getId());
            userInfo.setUsername(user1.getUsername());
            try{
                String token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), 30);
                return token;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }
}
