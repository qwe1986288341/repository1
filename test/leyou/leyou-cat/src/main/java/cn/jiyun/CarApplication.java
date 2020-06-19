package cn.jiyun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})  //配置开启主类信息
@EnableDiscoveryClient  //
@EnableFeignClients
public class CarApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarApplication.class,args);
    }
}
