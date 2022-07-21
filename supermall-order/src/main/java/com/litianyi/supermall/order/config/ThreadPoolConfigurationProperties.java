package com.litianyi.supermall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/6 5:14 PM
 */
@ConfigurationProperties(prefix = "thread")
@Data
public class ThreadPoolConfigurationProperties {
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors();
    private Integer maximumPoolSize = 8;
    private Integer keepAliveTime = 10;
}
