package com.grupocordillera.indicadores.service.factory;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculoInventarioStrategyTest {

    @Test
    void testCalcular() {
        var strategy = new CalculoInventarioStrategy(
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(1000)
        );
        BigDecimal resultado = strategy.calcular();
        assertNotNull(resultado);
        assertEquals(0, BigDecimal.valueOf(20.0).compareTo(resultado));
    }

    @Test
    void testCalcularConInventarioCero() {
        var strategy = new CalculoInventarioStrategy(
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        assertEquals(BigDecimal.ZERO, strategy.calcular());
    }

    @Test
    void testGetNombre() {
        var strategy = new CalculoInventarioStrategy(BigDecimal.TEN, BigDecimal.valueOf(100));
        assertEquals("Rotación de Inventario", strategy.getNombre());
    }

    @Test
    void testGetUnidad() {
        var strategy = new CalculoInventarioStrategy(BigDecimal.TEN, BigDecimal.valueOf(100));
        assertEquals("%", strategy.getUnidad());
    }
}
