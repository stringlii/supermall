package com.litianyi.supermall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
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
    RedissonClient redisson(@Value("${spring.redis.host}") String url,
                            @Value("${spring.redis.port}") Integer port,
                            @Value("${spring.redis.password}") String password) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + url + ":" + port).setPassword(password);
        return Redisson.create(config);
    }
}
