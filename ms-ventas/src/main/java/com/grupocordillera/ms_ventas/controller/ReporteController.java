package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.ReporteVentasDTO;
import com.grupocordillera.ms_ventas.dto.ResumenVentasDTO;
import com.grupocordillera.ms_ventas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas-por-sucursal")
    public ResponseEntity<List<ReporteVentasDTO>> reporteVentasPorSucursal() {
        return ResponseEntity.ok(reporteService.reporteVentasPorSucursal());
    }

    @GetMapping("/resumen-ventas")
    public ResponseEntity<ResumenVentasDTO> resumenVentas() {
        return ResponseEntity.ok(reporteService.resumenVentas());
    }

    @GetMapping("/ventas-periodo")
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
