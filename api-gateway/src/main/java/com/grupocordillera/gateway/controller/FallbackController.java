package com.grupocordillera.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/ventas")
    public ResponseEntity<Map<String, Object>> ventasFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Servicio de Ventas no disponible",
                "message", "El microservicio de ventas está temporalmente fuera de servicio. Intente nuevamente más tarde."
        ));
    }

    @GetMapping("/datos-org")
    public ResponseEntity<Map<String, Object>> datosOrgFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Servicio de Datos Organizacionales no disponible",
                "message", "El microservicio de datos organizacionales está temporalmente fuera de servicio."
        ));
    }

    @GetMapping("/indicadores")
    public ResponseEntity<Map<String, Object>> indicadoresFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Servicio de Indicadores no disponible",
                "message", "El microservicio de indicadores KPI está temporalmente fuera de servicio."
        ));
    }

    @GetMapping("/bff")
    public ResponseEntity<Map<String, Object>> bffFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "BFF no disponible",
                "message", "El Backend For Frontend está temporalmente fuera de servicio."
        ));
    }
}
