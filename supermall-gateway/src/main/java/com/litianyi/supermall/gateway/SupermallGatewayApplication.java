package com.litianyi.supermall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Properties;

@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SupermallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermallGatewayApplication.class, args);
    }

}
