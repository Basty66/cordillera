package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.ClimaDTO;
import com.grupocordillera.ms_ventas.service.ClimaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clima")
@Tag(name = "Clima", description = "Clima en tiempo real por sucursal (OpenWeatherMap)")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    @GetMapping("/sucursales")
    @Operation(summary = "Obtener clima de sucursales", description = "Retorna el clima actual de todas las ciudades con sucursal")
    public ResponseEntity<List<ClimaDTO>> obtenerClimaSucursales() {
        return ResponseEntity.ok(climaService.obtenerClimaSucursales());
    }

    @GetMapping("/ciudad/{ciudad}")
    @Operation(summary = "Obtener clima por ciudad", description = "Retorna el clima actual de una ciudad específica")
    public ResponseEntity<ClimaDTO> obtenerClimaPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(climaService.obtenerClimaPorCiudad(ciudad));
    }
}
