package com.litianyi.supermall.gateway.filters.deprecated;

import com.alibaba.fastjson.JSON;
import com.litianyi.supermall.gateway.filters.deprecated.constant.FilterConstant;
import com.litianyi.supermall.gateway.context.BaseContextHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/1/7 12:19 PM
 */
@Slf4j
public class WebLoggerStarFilter implements GlobalFilter, Ordered {

    private int order;

    public WebLoggerStarFilter(int order) {
        this.order = order;
    }

    private void startLogInfo(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        String url = request.getURI().toString();
        String params = JSON.toJSONString(request.getQueryParams());
        String method = request.getMethodValue();
        String headers = JSON.toJSONString(request.getHeaders().toSingleValueMap());

        byte[] body = exchange.getAttributeOrDefault(FilterConstant.CACHED_REQUEST_BODY_OBJECT_KEY, new byte[0]);
        String requestBody = new String(body);

        StringBuilder builder = new StringBuilder();
        builder.append("\n****************************************请求路径:[{}], 请求开始****************************************");
        builder.append("\nheaders: {}");
        builder.append("\nmethod: {}");
        builder.append("\nparams: {}");
        builder.append("\nrequestBody: {}");
        log.info(String.valueOf(builder), url, headers, method, params, requestBody);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //前置过滤
        BaseContextHandler.getContextMap().put("beginTime", Instant.now());
        this.startLogInfo(exchange);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
