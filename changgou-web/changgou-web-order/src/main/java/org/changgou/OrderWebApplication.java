package org.changgou;

import org.changgou.framework.intercerptor.FeignOauth2RequestInterceptor;
import org.changgou.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/24  23:05
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"org.changgou.order.feign","org.changgou.user.feign"})
@EnableEurekaClient
public class OrderWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderWebApplication.class, args);
    }

    /**
     * feign 拦截器,在feign的调用中附加请求头信息
     * @return
     */
    @Bean
    public FeignOauth2RequestInterceptor feignInterceptor(){
        return new FeignOauth2RequestInterceptor();
    }

}
