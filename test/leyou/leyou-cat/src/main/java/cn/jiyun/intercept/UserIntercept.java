package cn.jiyun.intercept;

import cn.jiyun.common.pojo.utils.CookieUtils;
import cn.jiyun.entity.UserInfo;
import cn.jiyun.properties.JwtProperties;
import cn.jiyun.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserIntercept extends HandlerInterceptorAdapter {

    @Autowired
    JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //通过cookie工具类和request请求 可以使用工具类CookieUtils来获取它的token
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
        if (token==null){
            //设置错误状态码为401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }else{
            //正确我们就解析token
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //将数据存放在ThreadLocal中
            THREAD_LOCAL.set(userInfo);
            return true;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        //到这个时候 线程已经请求万 这个时候我们就可以清除我们的ThreandLocal
        THREAD_LOCAL.remove();
    }

    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }
}
