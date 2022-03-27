package com.litianyi.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/3/27 3:02 PM
 */
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class ElasticSearchProperties {
    private String hostname;
    private Integer port;
    private String username;
    private String password;
}
