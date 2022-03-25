package com.litianyi.supermall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/2/15 4:24 PM
 */
@Configuration
public class RedissonConfiguration {

    /**
     * 使用 RedissonClient 操作 redisson
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        return Redisson.create(config);
    }
}
