package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.service.SucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "Operaciones de sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    @Operation(summary = "Listar sucursales", description = "Retorna una lista de todas las sucursales")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de sucursales obtenida exitosamente")
    })
    public ResponseEntity<List<Sucursal>> listarSucursales() {
        return ResponseEntity.ok(sucursalService.obtenerTodas());
    }

    @GetMapping("/count")
    @Operation(summary = "Contar sucursales", description = "Retorna el número total de sucursales")
    public ResponseEntity<Long> contarSucursales() {
        return ResponseEntity.ok(sucursalService.contarSucursales());
    }

    @PostMapping
    @Operation(summary = "Crear sucursal", description = "Crea una nueva sucursal manualmente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<Sucursal> crearSucursal(@RequestBody Sucursal sucursal) {
        return ResponseEntity.ok(sucursalService.guardarSucursal(sucursal));
    }

    @PostMapping("/generar/{cantidad}")
    @Operation(summary = "Generar sucursales masivas", description = "Genera una cantidad especifica de sucursales de forma automatica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursales generadas exitosamente"),
        @ApiResponse(responseCode = "400", description = "Cantidad invalida")
    })
    public ResponseEntity<String> generarMasivas(@PathVariable int cantidad) {
        return ResponseEntity.ok(sucursalService.generarDatosMasivos(cantidad));
    }
}