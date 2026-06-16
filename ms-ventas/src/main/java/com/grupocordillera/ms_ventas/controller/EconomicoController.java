package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.AjustePrecioDTO;
import com.grupocordillera.ms_ventas.dto.IndicadorEconomicoDTO;
import com.grupocordillera.ms_ventas.service.AjustePrecioService;
import com.grupocordillera.ms_ventas.service.MindicadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/economico")
@Tag(name = "Económico", description = "Indicadores económicos chilenos (UF, USD, UTM, IPC)")
public class EconomicoController {

    private final MindicadorService mindicadorService;
    private final AjustePrecioService ajustePrecioService;

    public EconomicoController(MindicadorService mindicadorService, AjustePrecioService ajustePrecioService) {
        this.mindicadorService = mindicadorService;
        this.ajustePrecioService = ajustePrecioService;
    }

    @GetMapping("/indicadores")
    @Operation(summary = "Obtener indicadores económicos", description = "Retorna UF, USD, UTM, IPC desde mindicador.cl con caché")
    public ResponseEntity<IndicadorEconomicoDTO> obtenerIndicadores() {
        return ResponseEntity.ok(mindicadorService.obtenerIndicadores());
    }

    @PostMapping("/ajustar-precios-uf")
    @Operation(summary = "Ajustar precios por UF", description = "Actualiza precios de todos los productos según variación de la UF")
    public ResponseEntity<AjustePrecioDTO> ajustarPreciosPorUf() {
        return ResponseEntity.ok(ajustePrecioService.ajustarPreciosPorUf());
    }
}
