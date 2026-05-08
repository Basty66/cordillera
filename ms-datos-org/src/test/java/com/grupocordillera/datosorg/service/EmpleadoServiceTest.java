package com.grupocordillera.datosorg.service;

import com.grupocordillera.datosorg.repository.EmpleadoRepository;
import com.grupocordillera.datosorg.service.factory.EmpleadoFactory;
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
    void testGenerarMasivos() {
        when(empleadoFactory.crearEmpleadosMasivos(5)).thenReturn(List.of());
        when(empleadoRepository.saveAll(any())).thenReturn(List.of());

        String resultado = empleadoService.generarMasivos(5);

        assertEquals("Se generaron 5 empleados correctamente.", resultado);
        verify(empleadoRepository, times(1)).saveAll(any());
    }
}
