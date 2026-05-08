package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.VentaRequestDTO;
import com.grupocordillera.ms_ventas.entity.Venta;
import com.grupocordillera.ms_ventas.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public List<Venta> listarTodas() {
        return ventaService.obtenerTodas();
    }

    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody VentaRequestDTO request) {
        Venta nuevaVenta = ventaService.registrarVenta(request);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }
}