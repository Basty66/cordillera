package com.grupocordillera.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Tag(name = "Fallback", description = "Endpoints de fallback del Circuit Breaker")
public class FallbackController {

    @GetMapping("/ventas")
    @Operation(summary = "Fallback ventas", description = "Respuesta cuando el microservicio de ventas no esta disponible")
    @ApiResponses({
        @ApiResponse(responseCode = "503", description = "Servicio de Ventas no disponible")
    })
    public ResponseEntity<Map<String, Object>> ventasFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Servicio de Ventas no disponible",
                "message", "El microservicio de ventas está temporalmente fuera de servicio. Intente nuevamente más tarde."
        ));
    }

    @GetMapping("/datos-org")
    @Operation(summary = "Fallback datos org", description = "Respuesta cuando el microservicio de datos organizacionales no esta disponible")
    @ApiResponses({
        @ApiResponse(responseCode = "503", description = "Servicio de Datos Organizacionales no disponible")
    })
    public ResponseEntity<Map<String, Object>> datosOrgFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Servicio de Datos Organizacionales no disponible",
                "message", "El microservicio de datos organizacionales está temporalmente fuera de servicio."
        ));
    }

    @GetMapping("/indicadores")
    @Operation(summary = "Fallback indicadores", description = "Respuesta cuando el microservicio de indicadores no esta disponible")
    @ApiResponses({
        @ApiResponse(responseCode = "503", description = "Servicio de Indicadores no disponible")
    })
    public ResponseEntity<Map<String, Object>> indicadoresFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Servicio de Indicadores no disponible",
                "message", "El microservicio de indicadores KPI está temporalmente fuera de servicio."
        ));
    }

    @GetMapping("/bff")
    @Operation(summary = "Fallback bff", description = "Respuesta cuando el BFF no esta disponible")
    @ApiResponses({
        @ApiResponse(responseCode = "503", description = "BFF no disponible")
    })
    public ResponseEntity<Map<String, Object>> bffFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "BFF no disponible",
                "message", "El Backend For Frontend está temporalmente fuera de servicio."
        ));
    }
}
