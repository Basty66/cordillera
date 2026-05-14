package com.grupocordillera.datosorg.controller;

import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.service.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
@Tag(name = "Empleados", description = "Operaciones de empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    @Operation(summary = "Listar empleados", description = "Retorna una lista de todos los empleados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida exitosamente")
    })
    public ResponseEntity<List<Empleado>> listar() {
        return ResponseEntity.ok(empleadoService.obtenerTodos());
    }

    @GetMapping("/count")
    @Operation(summary = "Contar empleados", description = "Retorna el total de empleados activos")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Total obtenido exitosamente") })
    public ResponseEntity<Long> contar() {
        return ResponseEntity.ok(empleadoService.contarActivos());
    }

    @PostMapping
    @Operation(summary = "Crear empleado", description = "Crea un nuevo empleado en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empleado creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<Empleado> crear(@RequestBody Empleado empleado) {
        return ResponseEntity.ok(empleadoService.guardar(empleado));
    }

    @PostMapping("/generar/{cantidad}")
    @Operation(summary = "Generar empleados masivos", description = "Genera una cantidad especifica de empleados de forma automatica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empleados generados exitosamente"),
        @ApiResponse(responseCode = "400", description = "Cantidad invalida")
    })
    public ResponseEntity<String> generarMasivos(@PathVariable int cantidad) {
        return ResponseEntity.ok(empleadoService.generarMasivos(cantidad));
    }
}
