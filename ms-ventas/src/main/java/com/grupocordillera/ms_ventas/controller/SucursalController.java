package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    // 1. Endpoint para listar todas las sucursales (GET)
    @GetMapping
    public ResponseEntity<List<Sucursal>> listarSucursales() {
        return ResponseEntity.ok(sucursalService.obtenerTodas());
    }

    // 2. Endpoint para crear una sucursal manualmente (POST)
    @PostMapping
    public ResponseEntity<Sucursal> crearSucursal(@RequestBody Sucursal sucursal) {
        return ResponseEntity.ok(sucursalService.guardarSucursal(sucursal));
    }

    // 3. NUEVO: Endpoint para inyectar datos masivos automáticamente (POST)
    @PostMapping("/generar/{cantidad}")
    public ResponseEntity<String> generarMasivas(@PathVariable int cantidad) {
        return ResponseEntity.ok(sucursalService.generarDatosMasivos(cantidad));
    }
}