package com.grupocordillera.datosorg.service;

import com.grupocordillera.datosorg.repository.EmpleadoRepository;
import com.grupocordillera.datosorg.service.factory.EmpleadoFactory;
import com.grupocordillera.datosorg.entity.Empleado;
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
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;
    @Mock
    private EmpleadoFactory empleadoFactory;

    @InjectMocks
    private EmpleadoService empleadoService;

    @Test
    void testObtenerTodos() {
        when(empleadoRepository.findAll()).thenReturn(List.of(new Empleado(), new Empleado()));

        var result = empleadoService.obtenerTodos();

        assertEquals(2, result.size());
    }

    @Test
    void testContarActivos() {
        when(empleadoRepository.countByActivoTrue()).thenReturn(5L);

        long result = empleadoService.contarActivos();

        assertEquals(5L, result);
    }

    @Test
    void testGuardar() {
        Empleado emp = new Empleado();
        emp.setNombre("Test");
        when(empleadoRepository.save(any())).thenReturn(emp);

        Empleado result = empleadoService.guardar(emp);

        assertEquals("Test", result.getNombre());
    }

    @Test
    void testGenerarMasivos() {
        when(empleadoFactory.crearEmpleadosMasivos(5)).thenReturn(List.of());
        when(empleadoRepository.saveAll(any())).thenReturn(List.of());

        String resultado = empleadoService.generarMasivos(5);

        assertEquals("Se generaron 5 empleados correctamente.", resultado);
        verify(empleadoRepository, times(1)).saveAll(any());
    }
}
