package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.dto.IndicadorEconomicoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MindicadorServiceTest {

    private MindicadorService mindicadorService;

    @BeforeEach
    void setUp() {
        mindicadorService = new MindicadorService();
    }

    @Test
    void testObtenerIndicadores_returnsData() {
        IndicadorEconomicoDTO result = mindicadorService.obtenerIndicadores();

        assertNotNull(result);
        assertNotNull(result.fecha());
        assertNotNull(result.indicadores());
        assertFalse(result.indicadores().isEmpty());
    }

    @Test
    void testObtenerIndicadores_hasRequiredKeys() {
        IndicadorEconomicoDTO result = mindicadorService.obtenerIndicadores();

        Map<String, IndicadorEconomicoDTO.IndicadorValor> ind = result.indicadores();
        assertTrue(ind.containsKey("UF") || ind.containsKey("DOLAR") || ind.containsKey("UTM") || ind.containsKey("IPC"));
    }

    @Test
    void testObtenerIndicadores_indicadoresHaveValidStructure() {
        IndicadorEconomicoDTO result = mindicadorService.obtenerIndicadores();

        assertFalse(result.indicadores().isEmpty());
        result.indicadores().forEach((key, val) -> {
            assertNotNull(val.nombre());
            assertNotNull(val.unidad());
            assertNotNull(val.valor());
        });
    }

    @Test
    void testObtenerUfActual_returnsPositiveValue() {
        BigDecimal uf = mindicadorService.obtenerUfActual();

        assertNotNull(uf);
        assertTrue(uf.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testLimpiarCache_doesNotThrow() {
        assertDoesNotThrow(() -> mindicadorService.limpiarCache());
    }
}
