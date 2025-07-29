package com.relatia.gateway_server.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
public class RouteConfig {
    @Bean
    RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r
                        .path("/relatia/customers/**")
                        .filters(f -> f.rewritePath("/relatia/customers/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(config->config.setName("customerCircuitBreaker").setFallbackUri("forward:/contact-support"))
                                        .retry(config->config.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofSeconds(1),Duration.ofSeconds(10),2,true))
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://customer-service"))
                .route(r -> r
                        .path("/relatia/notifications/**")
                        .filters(f -> f.rewritePath("/relatia/notifications/(?<segment>.*)", "/${segment}")
                                .circuitBreaker(config->config.setName("notificationCircuitBreaker").setFallbackUri("forward:/contact-support"))
                                .retry(config->config.setRetries(3).setMethods(HttpMethod.GET).setBackoff(Duration.ofSeconds(1),Duration.ofSeconds(10),2,true))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver()))
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://notification-service"))
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
                .defaultIfEmpty("anonymous");
    }
}
