package com.grupocordillera.indicadores.service.factory;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculoVentasStrategyTest {

    @Test
    void testCalcularTicketPromedio() {
        CalculoVentasStrategy strategy = new CalculoVentasStrategy(
                BigDecimal.valueOf(1000000), 50L);

        assertEquals(BigDecimal.valueOf(20000).setScale(2), strategy.calcular());
    }

    @Test
    void testCalcularSinTransacciones() {
        CalculoVentasStrategy strategy = new CalculoVentasStrategy(
                BigDecimal.valueOf(1000000), 0L);

        assertEquals(BigDecimal.ZERO, strategy.calcular());
    }

    @Test
    void testCalcularSinVentas() {
        CalculoVentasStrategy strategy = new CalculoVentasStrategy(
                BigDecimal.ZERO, 10L);

        assertEquals(0, strategy.calcular().compareTo(BigDecimal.ZERO));
    }
}
