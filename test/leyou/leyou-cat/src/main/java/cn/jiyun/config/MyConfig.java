package cn.jiyun.config;

import cn.jiyun.intercept.UserIntercept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configurable
@Component
public class MyConfig implements WebMvcConfigurer {

    @Autowired
    UserIntercept userIntercept;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userIntercept).addPathPatterns("/**");
    }
}
