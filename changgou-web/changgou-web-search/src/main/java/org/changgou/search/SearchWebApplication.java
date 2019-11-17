package org.changgou.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/18  21:12
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"org.changgou.search.feign"})
@EnableEurekaClient
public class SearchWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchWebApplication.class,args);
    }
}
