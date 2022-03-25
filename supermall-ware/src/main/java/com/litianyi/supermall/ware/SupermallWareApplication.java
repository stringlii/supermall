package com.litianyi.supermall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients("com.litianyi.supermall.ware.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class SupermallWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupermallWareApplication.class, args);
	}

}
