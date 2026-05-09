package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.DashboardDTO;
import com.grupocordillera.bff.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bff")
@Tag(name = "Dashboard", description = "Dashboard consolidado del BFF")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Obtener dashboard", description = "Retorna el dashboard consolidado con datos de todos los microservicios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard obtenido exitosamente"),
        @ApiResponse(responseCode = "503", description = "Uno o mas microservicios no estan disponibles")
    })
    public ResponseEntity<DashboardDTO> obtenerDashboard() {
        return ResponseEntity.ok(dashboardService.obtenerDashboard());
    }
}
