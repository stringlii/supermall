package com.litianyi.supermall.gateway.filters.deprecated;

import com.litianyi.supermall.gateway.filters.deprecated.constant.FilterConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @description 将 request body 中的内容 copy 一份，记录到 exchange 的一个自定义属性中
 */
@Slf4j
public class CacheRequestBodyGlobalFilter implements GlobalFilter, Ordered {

    private int order;

    public CacheRequestBodyGlobalFilter(int order) {
        this.order = order;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 将 request body 中的内容 copy 一份，记录到 exchange 的一个自定义属性中
        byte[] cachedRequestBodyObject = exchange.getAttributeOrDefault(FilterConstant.CACHED_REQUEST_BODY_OBJECT_KEY, new byte[0]);
        // 如果已经缓存过，略过
        if (cachedRequestBodyObject.length > 0) {
            return chain.filter(exchange);
        }
        // 如果没有缓存过，获取字节数组存入 exchange 的自定义属性中
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                }).defaultIfEmpty(new byte[0])
                .doOnNext(bytes -> exchange.getAttributes().put(FilterConstant.CACHED_REQUEST_BODY_OBJECT_KEY, bytes))
                .then(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}


