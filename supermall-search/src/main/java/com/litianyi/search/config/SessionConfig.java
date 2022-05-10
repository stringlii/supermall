package com.litianyi.search.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.litianyi.common.constant.AuthServerConstant;
import com.litianyi.common.constant.DomainConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/4/20 4:02 PM
 */
@Configuration
public class SessionConfig {

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setDomainName(DomainConstant.SUPERMALL);
        serializer.setCookieMaxAge(AuthServerConstant.COOKIE_MAX_AGE);
        return serializer;
    }
}
