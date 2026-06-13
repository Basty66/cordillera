package com.grupocordillera.indicadores.service.factory;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculoRentabilidadStrategyTest {

    @Test
    void testCalcularMargen() {
        var strategy = new CalculoRentabilidadStrategy(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(60000)
        );
        BigDecimal resultado = strategy.calcular();
        assertEquals(0, BigDecimal.valueOf(40.0).compareTo(resultado));
    }

    @Test
    void testCalcularConIngresosCero() {
        var strategy = new CalculoRentabilidadStrategy(
                BigDecimal.ZERO,
                BigDecimal.valueOf(50000)
        );
        assertEquals(BigDecimal.ZERO, strategy.calcular());
    }

    @Test
    void testCalcularConCostosMayoresAIngresos() {
        var strategy = new CalculoRentabilidadStrategy(
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(80000)
        );
        BigDecimal resultado = strategy.calcular();
        assertTrue(resultado.compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    void testCalcularMargenCero() {
        var strategy = new CalculoRentabilidadStrategy(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(100000)
        );
        assertEquals(0, strategy.calcular().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testCalcularMargenCienPorciento() {
        var strategy = new CalculoRentabilidadStrategy(
                BigDecimal.valueOf(100000),
                BigDecimal.ZERO
        );
        assertEquals(0, BigDecimal.valueOf(100).compareTo(strategy.calcular()));
    }

    @Test
    void testGetNombre() {
        var strategy = new CalculoRentabilidadStrategy(BigDecimal.TEN, BigDecimal.ONE);
        assertEquals("Margen de Rentabilidad", strategy.getNombre());
    }

    @Test
    void testGetUnidad() {
        var strategy = new CalculoRentabilidadStrategy(BigDecimal.TEN, BigDecimal.ONE);
        assertEquals("%", strategy.getUnidad());
    }
}
