package com.grupocordillera.datosorg.controller;

import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<Empleado>> listar() {
        return ResponseEntity.ok(empleadoService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<Empleado> crear(@RequestBody Empleado empleado) {
        return ResponseEntity.ok(empleadoService.guardar(empleado));
    }

    @PostMapping("/generar/{cantidad}")
    public ResponseEntity<String> generarMasivos(@PathVariable int cantidad) {
        return ResponseEntity.ok(empleadoService.generarMasivos(cantidad));
    }
}
