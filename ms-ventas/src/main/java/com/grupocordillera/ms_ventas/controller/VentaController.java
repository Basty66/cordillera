package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.VentaRequestDTO;
import com.grupocordillera.ms_ventas.entity.Venta;
import com.grupocordillera.ms_ventas.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Operaciones de ventas")
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    @Operation(summary = "Listar ventas", description = "Retorna ventas con paginacion opcional")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ventas obtenidas exitosamente")
    })
    public ResponseEntity<?> listarTodas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamaño) {
        if (pagina == 0 && tamaño == Integer.MAX_VALUE) {
            return ResponseEntity.ok(ventaService.obtenerTodas());
        }
        return ResponseEntity.ok(ventaService.obtenerPaginadas(pagina, tamaño));
    }

    @PostMapping
    @Operation(summary = "Crear una venta", description = "Registra una nueva venta en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Venta creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<Venta> crearVenta(@RequestBody VentaRequestDTO request) {
        Venta nuevaVenta = ventaService.registrarVenta(request);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }
}