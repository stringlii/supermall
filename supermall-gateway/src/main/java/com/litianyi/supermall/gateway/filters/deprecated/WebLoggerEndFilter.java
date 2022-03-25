package com.litianyi.supermall.gateway.filters.deprecated;

import com.litianyi.supermall.gateway.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/7 12:19 PM
 */
@Slf4j
public class WebLoggerEndFilter implements GlobalFilter, Ordered {

    private int order;

    public WebLoggerEndFilter(int order) {
        this.order = order;
    }

    private void endLogInfo(ServerWebExchange exchange, long time) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().toString();
        String attribute = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR).toString();

        StringBuilder builder = new StringBuilder();
        builder.append("****************************************请求路径:[{}]请求结束, 耗时: [{}]ms****************************************\n");
        builder.append("gateway_request_url: {}\n");
        log.info(String.valueOf(builder), url, time, attribute);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //后置过滤
        Instant instant = (Instant) BaseContextHandler.getContextMap().get("beginTime");
        endLogInfo(exchange, Duration.between(instant, Instant.now()).toMillis());
        BaseContextHandler.remove();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
