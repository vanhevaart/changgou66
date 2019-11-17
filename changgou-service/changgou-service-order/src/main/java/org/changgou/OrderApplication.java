package org.changgou;


import org.changgou.framework.intercerptor.FeignOauth2RequestInterceptor;
import org.changgou.order.config.ResourceServerConfig;
import org.changgou.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/24  0:31
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"org.changgou.order.dao"})
@EnableFeignClients(basePackages = {"org.changgou.goods.feign","org.changgou.user.feign"})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }

    /**
     * token校验对象
     * @return
     */
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

    /**
     * 分布式ID
     * @return
     */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1);
    }
}
