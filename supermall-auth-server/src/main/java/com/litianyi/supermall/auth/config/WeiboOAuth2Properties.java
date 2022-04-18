package com.litianyi.supermall.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/15 22:36
 */
@Data
@ConfigurationProperties("oauth2.weibo")
public class WeiboOAuth2Properties {

    private String clientId;

    private String clientSecret;

    private String grantType;

    private String redirectUri;

    private String weiboAccessTokenUrl;
}
