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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    @Test
    void testObtenerTodas() {
        when(sucursalRepository.findAll()).thenReturn(List.of(new Sucursal(), new Sucursal()));
        assertEquals(2, sucursalService.obtenerTodas().size());
    }

    @Test
    void testGuardarSucursal() {
        Sucursal s = new Sucursal();
        s.setNombre("Sucursal Nueva");
        when(sucursalRepository.save(any())).thenReturn(s);
        var result = sucursalService.guardarSucursal(s);
        assertEquals("Sucursal Nueva", result.getNombre());
    }

    @Test
    void testGenerarDatosMasivos() {
        when(sucursalRepository.saveAll(anyList())).thenReturn(null);

        String result = sucursalService.generarDatosMasivos(5);

        assertEquals("¡Se insertaron 5 sucursales falsas con éxito!", result);
        verify(sucursalRepository, times(1)).saveAll(anyList());
    }
}
