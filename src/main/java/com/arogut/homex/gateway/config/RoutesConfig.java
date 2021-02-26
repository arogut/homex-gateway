package com.arogut.homex.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("devices-message", r -> r.path("/devices/{id}/measurement")
                        .uri("http://homex-data:8080"))
                .route("devices-register", r -> r.path("/devices/auth")
                        .uri("no://op"))
                .build();
    }
}
