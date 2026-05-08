package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @PostMapping("/generar/{cantidad}")
    public ResponseEntity<String> generar(@PathVariable int cantidad) {
        return ResponseEntity.ok(productoService.generarProductosMasivos(cantidad));
    }
}