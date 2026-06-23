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
                        .path("/api/ventas/**", "/api/productos/**", "/api/sucursales/**",
                              "/api/reportes/ventas-*", "/api/reportes/resumen-*",
                              "/api/reportes/top-*", "/api/economico/**", "/api/clima/**")
                        .filters(f -> f.circuitBreaker(cb -> cb
                                .setName("ms-ventas-cb")
                                .setFallbackUri("forward:/fallback/ventas")))
                        .uri("http://ms-ventas:8081"))

                .route("ms-datos-org", r -> r
                        .path("/api/departamentos/**", "/api/empleados/**")
                        .filters(f -> f.circuitBreaker(cb -> cb
                                .setName("ms-datos-org-cb")
                                .setFallbackUri("forward:/fallback/datos-org")))
                        .uri("http://ms-datos-org:8082"))

                .route("ms-indicadores", r -> r
                        .path("/api/indicadores/**", "/api/indicadores/categorias/**")
                        .filters(f -> f.circuitBreaker(cb -> cb
                                .setName("ms-indicadores-cb")
                                .setFallbackUri("forward:/fallback/indicadores")))
                        .uri("http://ms-indicadores:8083"))

                .route("bff-auth", r -> r
                        .path("/api/auth/**")
                        .uri("http://bff:8090"))

                .route("bff-tickets", r -> r
                        .path("/api/tickets/**")
                        .uri("http://bff:8090"))

                .route("bff-reportes", r -> r
                        .path("/api/reportes/**")
                        .uri("http://bff:8090"))

                .route("bff", r -> r
                        .path("/api/bff/**")
                        .filters(f -> f.circuitBreaker(cb -> cb
                                .setName("bff-cb")
                                .setFallbackUri("forward:/fallback/bff")))
                        .uri("http://bff:8090"))

                .build();
    }
}
