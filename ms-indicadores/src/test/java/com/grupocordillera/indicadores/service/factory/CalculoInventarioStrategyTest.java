package com.grupocordillera.indicadores.service.factory;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalculoInventarioStrategyTest {

    private final CalculoInventarioStrategy strategy = new CalculoInventarioStrategy();

    @Test
    void testCalcular() {
        Map<String, Object> params = Map.of(
                "productosVendidos", 100,
                "inventarioPromedio", 500
        );
        BigDecimal resultado = strategy.calcular(params);
        assertNotNull(resultado);
        assertEquals(0, BigDecimal.valueOf(20.0).compareTo(resultado));
    }

    @Test
    void testGetNombre() {
        assertEquals("Rotacion de Inventario", strategy.getNombre());
    }

    @Test
    void testGetUnidad() {
        assertEquals("%", strategy.getUnidad());
    }
}
