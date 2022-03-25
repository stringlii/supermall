package com.litianyi.supermall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients("com.litianyi.supermall.product.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class SupermallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermallProductApplication.class, args);
    }

}
