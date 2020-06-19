package com.leyou.filter;

import cn.jiyun.common.pojo.utils.CookieUtils;
import cn.jiyun.entity.UserInfo;
import cn.jiyun.utils.JwtUtils;
import com.leyou.properties.FilterProperties;
import com.leyou.properties.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sun.misc.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class})
public class UserFilter extends ZuulFilter {

    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    FilterProperties filterProperties;
    // pre路由前置过滤器 post后置过滤器 运行过滤器 route

    @Override
    public String filterType() {
        return "pre";
    }

    //过滤器等级
    @Override
    public int filterOrder() {
        return 20;
    }


    //是否过滤
    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        StringBuffer requestURL = request.getRequestURL();
        List<String> allowPaths = filterProperties.getAllowPaths();
        for (String allowPath : allowPaths) {
            if(allowPath.equals(requestURL)){
                return false;
            }
        }
        return true;
    }

    //过滤之后运行的代码
    @Override
    public Object run() throws ZuulException {

        //token来自于我们的cookie  cookie 来自于我们的request
        //通过zuul获取上下文对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");

        //进行登录的验证
        UserInfo userInfo = null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            if(userInfo==null){
                requestContext.setSendZuulResponse(false);
                requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            e.printStackTrace();
        }
        return null;
    }
}
