package com.grupocordillera.datosorg.service;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.repository.DepartamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartamentoServiceTest {

    @Mock
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private DepartamentoService departamentoService;

    @Test
    void testObtenerTodos() {
        when(departamentoRepository.findAll()).thenReturn(List.of(new Departamento(), new Departamento()));
        var result = departamentoService.obtenerTodos();
        assertEquals(2, result.size());
    }

    @Test
    void testObtenerTodos_whenEmpty_returnsEmptyList() {
        when(departamentoRepository.findAll()).thenReturn(List.of());
        var result = departamentoService.obtenerTodos();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGuardar() {
        Departamento dept = new Departamento();
        dept.setNombre("Ventas");
        dept.setDescripcion("Departamento de ventas");
        when(departamentoRepository.save(any())).thenReturn(dept);
        var result = departamentoService.guardar(dept);
        assertEquals("Ventas", result.getNombre());
        assertEquals("Departamento de ventas", result.getDescripcion());
    }

    @Test
    void testGuardar_conNombreLargo() {
        Departamento dept = new Departamento();
        dept.setNombre("Tecnologías de la Información y Comunicación");
        when(departamentoRepository.save(any())).thenReturn(dept);
        var result = departamentoService.guardar(dept);
        assertEquals("Tecnologías de la Información y Comunicación", result.getNombre());
    }
}
