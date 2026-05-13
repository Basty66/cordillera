package com.grupocordillera.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(FallbackController.class)
class FallbackControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testVentasFallback() {
        webTestClient.get().uri("/fallback/ventas")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Servicio de Ventas no disponible");
    }

    @Test
    void testDatosOrgFallback() {
        webTestClient.get().uri("/fallback/datos-org")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Servicio de Datos Organizacionales no disponible");
    }

    @Test
    void testIndicadoresFallback() {
        webTestClient.get().uri("/fallback/indicadores")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Servicio de Indicadores no disponible");
    }

    @Test
    void testBffFallback() {
        webTestClient.get().uri("/fallback/bff")
                .exchange()
                .expectStatus().isEqualTo(503)
                .expectBody()
                .jsonPath("$.error").isEqualTo("BFF no disponible");
    }
}
