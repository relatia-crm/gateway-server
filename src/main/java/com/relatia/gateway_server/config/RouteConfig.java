package com.relatia.gateway_server.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class RouteConfig {
    @Bean
    RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r
                        .path("/relatia/customers/**")
                        .filters(f -> f.rewritePath("/relatia/customers/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://customer-service"))
                .route(r -> r
                        .path("/relatia/notifications/**")
                        .filters(f -> f.rewritePath("/relatia/notifications/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://notification-service"))
                .build();
    }
}
