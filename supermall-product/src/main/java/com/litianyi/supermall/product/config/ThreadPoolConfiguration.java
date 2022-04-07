package com.litianyi.supermall.product.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/6 5:15 PM
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolConfigurationProperties.class)
public class ThreadPoolConfiguration {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigurationProperties properties) {
        return new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("supermall-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
