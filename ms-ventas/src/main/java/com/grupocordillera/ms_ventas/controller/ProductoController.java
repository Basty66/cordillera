package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Operaciones de productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar productos", description = "Retorna una lista de todos los productos disponibles")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    })
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @PostMapping("/generar/{cantidad}")
    @Operation(summary = "Generar productos masivos", description = "Genera una cantidad especifica de productos de forma automatica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos generados exitosamente"),
        @ApiResponse(responseCode = "400", description = "Cantidad invalida")
    })
    public ResponseEntity<String> generar(@PathVariable int cantidad) {
        return ResponseEntity.ok(productoService.generarProductosMasivos(cantidad));
    }
}