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
    void testGuardarSucursal_DatosValidos_Correcto() {
        Sucursal s = new Sucursal();
        s.setNombre("Sucursal Centro");
        s.setCiudad("Santiago");
        s.setDireccion("Av. Principal 123");

        when(sucursalRepository.save(any())).thenReturn(s);

        var result = sucursalService.guardarSucursal(s);

        assertEquals("Sucursal Centro", result.getNombre());
        assertEquals("Santiago", result.getCiudad());
    }

    @Test
    void testGuardarSucursal_SinNombre_Error() {
        Sucursal s = new Sucursal();
        s.setCiudad("Santiago");

        assertThrows(IllegalArgumentException.class, () -> sucursalService.guardarSucursal(s));
        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void testGuardarSucursal_NombreVacio_Error() {
        Sucursal s = new Sucursal();
        s.setNombre("   ");
        s.setCiudad("Santiago");

        assertThrows(IllegalArgumentException.class, () -> sucursalService.guardarSucursal(s));
        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void testGuardarSucursal_SinCiudad_Error() {
        Sucursal s = new Sucursal();
        s.setNombre("Sucursal Test");

        assertThrows(IllegalArgumentException.class, () -> sucursalService.guardarSucursal(s));
        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void testGuardarSucursal_CiudadVacia_Error() {
        Sucursal s = new Sucursal();
        s.setNombre("Sucursal Test");
        s.setCiudad("");

        assertThrows(IllegalArgumentException.class, () -> sucursalService.guardarSucursal(s));
        verify(sucursalRepository, never()).save(any());
    }

    @Test
    void testGenerarDatosMasivos() {
        when(sucursalRepository.saveAll(anyList())).thenReturn(null);

        String result = sucursalService.generarDatosMasivos(5);

        assertEquals("¡Se insertaron 5 sucursales falsas con éxito!", result);
        verify(sucursalRepository, times(1)).saveAll(anyList());
    }
}
