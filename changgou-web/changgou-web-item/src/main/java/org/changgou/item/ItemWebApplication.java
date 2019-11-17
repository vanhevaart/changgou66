package org.changgou.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  10:54
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = {"org.changgou.goods.feign"})
@RibbonClient
public class ItemWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemWebApplication.class, args);
    }
}
