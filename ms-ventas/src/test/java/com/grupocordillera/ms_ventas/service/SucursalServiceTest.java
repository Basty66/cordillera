package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.repository.SucursalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    @Test
    void testListarTodas() {
        when(sucursalRepository.findAll()).thenReturn(List.of(new Sucursal(), new Sucursal()));
        assertEquals(2, sucursalService.listarTodas().size());
    }

    @Test
    void testGuardar() {
        Sucursal s = new Sucursal();
        s.setNombre("Sucursal Nueva");
        when(sucursalRepository.save(any())).thenReturn(s);
        var result = sucursalService.guardar(s);
        assertEquals("Sucursal Nueva", result.getNombre());
    }
}
