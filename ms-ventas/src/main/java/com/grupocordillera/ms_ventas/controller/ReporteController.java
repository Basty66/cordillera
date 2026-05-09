package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.ReporteVentasDTO;
import com.grupocordillera.ms_ventas.dto.ResumenVentasDTO;
import com.grupocordillera.ms_ventas.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Reportes de ventas")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas-por-sucursal")
    @Operation(summary = "Ventas por sucursal", description = "Retorna un reporte de ventas agrupadas por sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte obtenido exitosamente")
    })
    public ResponseEntity<List<ReporteVentasDTO>> reporteVentasPorSucursal() {
        return ResponseEntity.ok(reporteService.reporteVentasPorSucursal());
    }

    @GetMapping("/resumen-ventas")
    @Operation(summary = "Resumen de ventas", description = "Retorna un resumen con totales de ventas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente")
    })
    public ResponseEntity<ResumenVentasDTO> resumenVentas() {
        return ResponseEntity.ok(reporteService.resumenVentas());
    }

    @GetMapping("/ventas-periodo")
    @Operation(summary = "Ventas por periodo", description = "Retorna el total de ventas en un rango de fechas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total de ventas del periodo obtenido"),
        @ApiResponse(responseCode = "400", description = "Formato de fecha invalido")
    })
    public ResponseEntity<BigDecimal> ventasPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fin) {
        BigDecimal total = reporteService.calcularVentasPorPeriodo(inicio, fin);
        if (total == null) {
            return ResponseEntity.ok(BigDecimal.ZERO);
        }
        return ResponseEntity.ok(total);
    }
}
