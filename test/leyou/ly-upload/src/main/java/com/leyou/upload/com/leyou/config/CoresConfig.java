package com.leyou.upload.com.leyou.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//被SpringMVC配置自动 加载
@Configuration
public class CoresConfig {

    @Bean
    public CorsFilter getCorsFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //分为简单请求和特殊请求
        //我们只需要考虑特殊请求
        //1.origin  允许的跨域的名称   尽量不要写*  如果写*的话cookie就不能使用
        corsConfiguration.addAllowedOrigin("*");
        //2.是否同意请求携带cookie
        corsConfiguration.setAllowCredentials(true);
        //3.允许跨域请求的类型 *所有请求访问类型的都可以访问
        corsConfiguration.addAllowedMethod("*");
        //4.允许跨域的请求的请求头  什么样的头信息都可以携带
        corsConfiguration.addAllowedHeader("*");


        UrlBasedCorsConfigurationSource cc = new UrlBasedCorsConfigurationSource();
        cc.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsFilter(cc);
    }
}
