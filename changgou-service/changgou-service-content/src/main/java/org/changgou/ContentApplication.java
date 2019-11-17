package org.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/14  23:03
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan("org.changgou.content.dao")
public class ContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
