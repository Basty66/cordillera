package com.grupocordillera.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ms-ventas", r -> r
                        .path("/api/ventas/**", "/api/productos/**", "/api/sucursales/**")
                        .filters(f -> f.circuitBreaker(cb -> cb
                                .setName("ms-ventas-cb")
                                .setFallbackUri("forward:/fallback/ventas")))
                        .uri("http://localhost:8081"))

                .route("ms-datos-org", r -> r
                        .path("/api/departamentos/**", "/api/empleados/**")
                        .filters(f -> f.circuitBreaker(cb -> cb
                                .setName("ms-datos-org-cb")
                                .setFallbackUri("forward:/fallback/datos-org")))
                        .uri("http://localhost:8082"))

                .route("ms-indicadores", r -> r
                        .path("/api/indicadores/**", "/api/indicadores/categorias/**")
                        .filters(f -> f.circuitBreaker(cb -> cb
                                .setName("ms-indicadores-cb")
                                .setFallbackUri("forward:/fallback/indicadores")))
                        .uri("http://localhost:8083"))

                .route("bff-auth", r -> r
                        .path("/api/auth/**")
                        .uri("http://localhost:8090"))

                .route("bff-tickets", r -> r
                        .path("/api/tickets/**")
                        .uri("http://localhost:8090"))

                .route("bff-reportes", r -> r
                        .path("/api/reportes/**")
                        .uri("http://localhost:8090"))

                .route("bff", r -> r
                        .path("/api/bff/**")
                        .filters(f -> f.circuitBreaker(cb -> cb
                                .setName("bff-cb")
                                .setFallbackUri("forward:/fallback/bff")))
                        .uri("http://localhost:8090"))

                .build();
    }
}
