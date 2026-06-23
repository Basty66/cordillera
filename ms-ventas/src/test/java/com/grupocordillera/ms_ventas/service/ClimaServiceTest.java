package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.dto.ClimaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClimaServiceTest {

    private ClimaService climaService;

    @BeforeEach
    void setUp() throws Exception {
        climaService = new ClimaService();
        var field = ClimaService.class.getDeclaredField("apiKey");
        field.setAccessible(true);
        field.set(climaService, "");
    }

    @Test
    void testObtenerClimaPorCiudad_returnsSimulatedData() {
        ClimaDTO result = climaService.obtenerClimaPorCiudad("Santiago");

        assertNotNull(result);
        assertEquals("Santiago", result.ciudad());
        assertNotNull(result.descripcion());
        assertNotNull(result.icono());
        assertTrue(result.temperatura().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.humedad().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.viento().compareTo(BigDecimal.ZERO) > 0);
        assertEquals("CL", result.pais());
    }

    @Test
    void testObtenerClimaPorCiudad_differentCiudades_returnDifferentData() {
        ClimaDTO santiago = climaService.obtenerClimaPorCiudad("Santiago");
        ClimaDTO valparaiso = climaService.obtenerClimaPorCiudad("Valparaíso");

        assertNotNull(santiago);
        assertNotNull(valparaiso);
        assertEquals("Santiago", santiago.ciudad());
        assertEquals("Valparaíso", valparaiso.ciudad());
    }

    @Test
    void testObtenerClimaSucursales_returnsAllCiudades() {
        List<ClimaDTO> result = climaService.obtenerClimaSucursales();

        assertNotNull(result);
        assertEquals(9, result.size());
        assertTrue(result.stream().anyMatch(c -> c.ciudad().equals("Santiago")));
        assertTrue(result.stream().anyMatch(c -> c.ciudad().equals("Concepción")));
        assertTrue(result.stream().anyMatch(c -> c.ciudad().equals("Antofagasta")));
        assertTrue(result.stream().anyMatch(c -> c.ciudad().equals("Puerto Montt")));
    }

    @Test
    void testObtenerClimaSucursales_allHaveValidData() {
        List<ClimaDTO> result = climaService.obtenerClimaSucursales();

        for (ClimaDTO c : result) {
            assertNotNull(c.ciudad());
            assertNotNull(c.descripcion());
            assertNotNull(c.icono());
            assertTrue(c.temperatura().compareTo(BigDecimal.ZERO) > 0);
            assertEquals("CL", c.pais());
        }
    }
}
