package com.ecommerce.filter;

import com.ecommerce.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthGlobalFilter(JwtUtil jwtUtil){
        this.jwtUtil=jwtUtil;
    }

    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/auth/login","/profile/register","/products/view","/profile/message"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        String path = exchange.getRequest().getPath().toString();

        if(OPEN_ENDPOINTS.stream().anyMatch(path::equals)){
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if(!jwtUtil.validateToken(token)){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String userId = jwtUtil.extractClaimByName(token,"userId");

        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id",userId)
                .build();
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        return chain.filter(modifiedExchange);

    }
}
