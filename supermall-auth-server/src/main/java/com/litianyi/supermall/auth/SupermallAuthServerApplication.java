package com.litianyi.supermall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients("com.litianyi.supermall.auth.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class SupermallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermallAuthServerApplication.class, args);
    }

}
