package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.ReporteVentasDTO;
import com.grupocordillera.ms_ventas.dto.ResumenVentasDTO;
import com.grupocordillera.ms_ventas.dto.TopProductoDTO;
import com.grupocordillera.ms_ventas.dto.VentaCategoriaDTO;
import com.grupocordillera.ms_ventas.dto.VentaMensualDTO;
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
@Tag(name = "Reportes", description = "Reportes de ventas, data warehouse")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas-por-sucursal")
    @Operation(summary = "Ventas por sucursal", description = "Retorna un reporte de ventas agrupadas por sucursal")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Reporte obtenido exitosamente") })
    public ResponseEntity<List<ReporteVentasDTO>> reporteVentasPorSucursal() {
        return ResponseEntity.ok(reporteService.reporteVentasPorSucursal());
    }

    @GetMapping("/resumen-ventas")
    @Operation(summary = "Resumen de ventas", description = "Retorna un resumen con totales de ventas")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente") })
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
        return ResponseEntity.ok(total == null ? BigDecimal.ZERO : total);
    }

    @GetMapping("/ventas-mensuales")
    @Operation(summary = "Ventas mensuales", description = "Data warehouse: ventas agregadas por mes para gráficos de tendencia")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Datos mensuales obtenidos") })
    public ResponseEntity<List<VentaMensualDTO>> ventasMensuales() {
        return ResponseEntity.ok(reporteService.ventasMensuales());
    }

    @GetMapping("/ventas-por-categoria")
    @Operation(summary = "Ventas por categoría", description = "Data warehouse: ventas agrupadas por categoría de producto")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Datos por categoría obtenidos") })
    public ResponseEntity<List<VentaCategoriaDTO>> ventasPorCategoria() {
        return ResponseEntity.ok(reporteService.ventasPorCategoria());
    }

    @GetMapping("/top-productos")
    @Operation(summary = "Top productos", description = "Data warehouse: ranking de productos más vendidos")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Ranking obtenido") })
    public ResponseEntity<List<TopProductoDTO>> topProductos(
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(reporteService.topProductos(limite));
    }
}
