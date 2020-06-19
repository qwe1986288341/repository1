package com.leyou.service;

import cn.jiyun.common.pojo.utils.CodecUtils;
import cn.jiyun.common.pojo.utils.NumberUtils;
import com.leyou.mapper.UserMapper;
import com.leyou.goods.pojo.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    AmqpTemplate amqpTemplate;
    @Autowired
    RedisTemplate redisTemplate;

    public Boolean findDate(String data, Integer type) {
        User user = new User();
        if(type==1){
            user.setUsername(data);
        }else if(type==2){
            user.setPhone(data);
        }else{
            return null;
        }
        List<User> list = userMapper.select(user);
        if(list.size()>=1){
            return false;
        }else {
            return true;
        }

    }

    public void send(String phone) {
        //这个方法就是用来发送短信
        //就是调用service曾生成一个验证码
        //将这个验证码用runbitmq的形式通知给我们的发送短信服务 sms
        //要发送什么 1.手机号 2.验证码
        //验证码应该设置一个过期时间 过了一定的时间就得不到我们的验证码
        //那么我们可以将一个值存入到redis中给它设置一个存活时间  我们就可以将code存入到redis中 然后再从redis中取
        //其中的key是有规则 一般以当前的服务为开头 user
        //再以内容作为中间值 code
        //最后以手机号作为结尾 phone
        String code = NumberUtils.generateCode(6);
        redisTemplate.opsForValue().set("user:code:phone:"+phone,code,5,TimeUnit.MINUTES);
        Map<String,String> map = new HashMap<>();
        map.put("phone",phone);
        map.put("code",code);
        amqpTemplate.convertAndSend("sms.verify.code",map);
    }

    public Boolean register(User user,String code) {
        //判断code是否还在使用期限
        String codeRedis = (String) redisTemplate.opsForValue().get("user:code:phone:" + user.getPhone());
        //判断传过来的验证码是否和我们redis中的验证码相同 如果相同则进行下边的操作
        if(code.equals(codeRedis)){
            //生成盐 salt  用于给密码加密
            String salt = CodecUtils.generateSalt();
            user.setSalt(salt);
            //生成加密之后的密码
            String password = user.getPassword();
            //加密的时候调用codecUtils中有一个方法md5hex
            String passwordnew = CodecUtils.md5Hex(password, salt);
            user.setPassword(passwordnew);
            //生成创建时间
            user.setCreated(new Date());
            //将user存放到tb_user中
            userMapper.insert(user);
        }

        return false;

    }

    public User queryUser(User user) {
        User user2 = new User();
        user2.setUsername(user.getUsername());
        User user3 = userMapper.selectOne(user2);
        System.out.println(user3);
        //判断user3是否为空,如果为空则说明数据出现了错误
        if(user3 == null){
            return null;
        }
        //然后判断密码
       if(user3.getPassword().equals(CodecUtils.md5Hex(user.getPassword(),user3.getSalt()))){
           System.out.println("密码错误");
           return null;
       }

        return user3;
    }
}
