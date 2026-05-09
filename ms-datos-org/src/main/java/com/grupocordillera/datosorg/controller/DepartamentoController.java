package com.grupocordillera.datosorg.controller;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
@Tag(name = "Departamentos", description = "Operaciones de departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @GetMapping
    @Operation(summary = "Listar departamentos", description = "Retorna una lista de todos los departamentos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de departamentos obtenida exitosamente")
    })
    public ResponseEntity<List<Departamento>> listar() {
        return ResponseEntity.ok(departamentoService.obtenerTodos());
    }

    @PostMapping
    @Operation(summary = "Crear departamento", description = "Crea un nuevo departamento en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Departamento creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<Departamento> crear(@RequestBody Departamento departamento) {
        return ResponseEntity.ok(departamentoService.guardar(departamento));
    }
}
