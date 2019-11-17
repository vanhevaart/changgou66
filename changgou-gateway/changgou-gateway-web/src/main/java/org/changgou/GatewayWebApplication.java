package org.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

/**
 * Author:  HZ
 * <p>
 * Create:  2019/8/20  19:33
 */
@SpringBootApplication
@EnableEurekaClient
public class GatewayWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayWebApplication.class,args);
    }

    /**
     * 根据IP进行限流
     * @return
     */
    @Bean(name = "ipKeyResolver")
    public KeyResolver keyResolver(){
        return exchange -> {
            String hostName = exchange.getRequest().getRemoteAddress().getAddress().getHostName();
            return Mono.just(hostName);
        };
    }
}
