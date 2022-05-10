package com.litianyi.supermall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.litianyi.supermall.cart.feign")
@EnableDiscoveryClient
public class SupermallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermallCartApplication.class, args);
    }

}
