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
public class Eureka02Application {

    public static void main(String[] args) {
        SpringApplication.run(Eureka02Application.class,args);
    }
}
