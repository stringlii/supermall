package com.litianyi.supermall.cart.config;

import com.litianyi.supermall.cart.interceptor.CartInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/5/8 9:27 PM
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor())
                .addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
