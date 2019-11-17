package org.changgou;

import org.changgou.framework.intercerptor.FeignOauth2RequestInterceptor;
import org.changgou.user.config.ResourceServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  21:03
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"org.changgou.user.dao"})
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }

    @Bean
    public ResourceServerConfig resourceServerConfig(){
        return new ResourceServerConfig();
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
