package com.litianyi.supermall.gateway.filters.deprecated;

import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class WrapperResponseGlobalFilter implements GlobalFilter, Ordered {

    private int order;

    public WrapperResponseGlobalFilter(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        // -1 is response write filter, must be called before that
        return this.order;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            //获取response的 返回数据
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                // probably should reuse buffers
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                //释放掉内存
                                DataBufferUtils.release(dataBuffer);
                                //s就是response的值，想修改、查看就随意而为了
                                String s = new String(content, StandardCharsets.UTF_8);
                                byte[] uppedContent = new String(content, StandardCharsets.UTF_8).getBytes();
                                return bufferFactory.wrap(uppedContent);
                            }));
                        }
                        // if body is not a flux. never got there.
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("ReplaceNullFilter 异常", e);
            return chain.filter(exchange);
        }
    }
}