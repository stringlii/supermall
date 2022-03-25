package com.litianyi.supermall.gateway.filters.deprecated;

import com.litianyi.supermall.gateway.filters.deprecated.CacheRequestBodyGlobalFilter;
import com.litianyi.supermall.gateway.filters.deprecated.WebLoggerEndFilter;
import com.litianyi.supermall.gateway.filters.deprecated.WebLoggerStarFilter;
import com.litianyi.supermall.gateway.filters.deprecated.WrapperResponseGlobalFilter;

/**
 * @author litianyi
 */
//@Configuration
public class GatewayConfiguration {

    /**
     * 获取requestBody的过滤器
     */
//    @Bean
    public CacheRequestBodyGlobalFilter globalCacheRequestBodyFilter() {
        return new CacheRequestBodyGlobalFilter(-10200);
    }

    /**
     * 获取response的过滤器
     */
//    @Bean
    public WrapperResponseGlobalFilter wrapperResponseGlobalFilter() {
        // -1 is response write filter, must be called before that
        return new WrapperResponseGlobalFilter(-2);
    }

    /**
     * 请求日志
     */
//    @Bean
    public WebLoggerStarFilter webLoggerStarFilter() {
        return new WebLoggerStarFilter(-10100);
    }

    /**
     * 请求日志
     */
//    @Bean
    public WebLoggerEndFilter webLoggerEndFilter() {
        return new WebLoggerEndFilter(10300);
    }
}

