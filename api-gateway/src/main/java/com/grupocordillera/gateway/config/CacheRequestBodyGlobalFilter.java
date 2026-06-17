package com.grupocordillera.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CacheRequestBodyGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -2147482647;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpMethod method = exchange.getRequest().getMethod();
        if (method == HttpMethod.GET || method == HttpMethod.HEAD || method == HttpMethod.DELETE || method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
        return org.springframework.cloud.gateway.support.ServerWebExchangeUtils.cacheRequestBody(exchange, (ServerHttpRequest cachedRequest) -> {
            ServerWebExchange cachedExchange = exchange.mutate().request(cachedRequest).build();
            return chain.filter(cachedExchange);
        });
    }
}
