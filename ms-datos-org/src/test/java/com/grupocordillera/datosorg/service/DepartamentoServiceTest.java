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
    void testListarTodos() {
        when(departamentoRepository.findAll()).thenReturn(List.of(new Departamento()));
        var result = departamentoService.listarTodos();
        assertEquals(1, result.size());
    }

    @Test
    void testGuardar() {
        Departamento dept = new Departamento();
        dept.setNombre("Ventas");
        when(departamentoRepository.save(any())).thenReturn(dept);
        var result = departamentoService.guardar(dept);
        assertEquals("Ventas", result.getNombre());
    }
}
