package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.TicketAnalyticsDTO;
import com.grupocordillera.bff.service.TicketAnalyticsService;
import com.grupocordillera.bff.service.TicketClassificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Analytics de Tickets", description = "Analítica inteligente de tickets")
public class TicketAnalyticsController {

    private final TicketAnalyticsService analyticsService;
    private final TicketClassificationService classificationService;

    public TicketAnalyticsController(TicketAnalyticsService analyticsService,
                                     TicketClassificationService classificationService) {
        this.analyticsService = analyticsService;
        this.classificationService = classificationService;
    }

    @GetMapping("/analytics")
    @Operation(summary = "Dashboard analítico de tickets", description = "Retorna estadísticas, tendencias y clasificación inteligente de tickets")
    public ResponseEntity<TicketAnalyticsDTO> obtenerAnalytics() {
        return ResponseEntity.ok(analyticsService.obtenerAnalytics());
    }

    @PostMapping("/clasificar")
    @Operation(summary = "Clasificar ticket", description = "Analiza el texto de un ticket y sugiere una categoría automáticamente")
    public ResponseEntity<Map<String, String>> clasificar(@RequestBody Map<String, String> body) {
        String titulo = body.getOrDefault("titulo", "");
        String descripcion = body.getOrDefault("descripcion", "");
        String categoria = classificationService.clasificar(titulo, descripcion);
        return ResponseEntity.ok(Map.of("categoria", categoria));
    }

    @GetMapping("/categorias")
    @Operation(summary = "Obtener categorías", description = "Lista las categorías disponibles para clasificación de tickets")
    public ResponseEntity<String[]> obtenerCategorias() {
        return ResponseEntity.ok(classificationService.obtenerCategorias());
    }
}
