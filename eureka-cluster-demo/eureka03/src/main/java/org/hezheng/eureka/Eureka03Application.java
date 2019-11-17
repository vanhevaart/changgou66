package org.hezheng.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/9/1  9:19
 */
@SpringBootApplication
@EnableEurekaServer
public class Eureka03Application {

    public static void main(String[] args) {
        SpringApplication.run(Eureka03Application.class,args);
    }
}
