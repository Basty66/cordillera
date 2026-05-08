package com.grupocordillera.indicadores.service.factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CalculoIndicadorFactoryTest {

    private CalculoIndicadorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CalculoIndicadorFactory();
    }

    @Test
    void testFactoryCreaEstrategiaVentas() {
        CalculoStrategy strategy = factory.crearStrategy("VENTAS",
                BigDecimal.valueOf(500000), BigDecimal.valueOf(100));

        assertNotNull(strategy);
        assertInstanceOf(CalculoVentasStrategy.class, strategy);
        assertEquals("Ticket Promedio", strategy.getNombre());
        assertEquals("CLP", strategy.getUnidad());
        assertEquals(BigDecimal.valueOf(5000).setScale(2), strategy.calcular());
    }

    @Test
    void testFactoryCreaEstrategiaInventario() {
        CalculoStrategy strategy = factory.crearStrategy("INVENTARIO",
                BigDecimal.valueOf(500000), BigDecimal.valueOf(1000000));

        assertNotNull(strategy);
        assertInstanceOf(CalculoInventarioStrategy.class, strategy);
        assertEquals("Rotación de Inventario", strategy.getNombre());
        assertEquals("%", strategy.getUnidad());
        assertEquals(BigDecimal.valueOf(50).setScale(2), strategy.calcular().setScale(2));
    }

    @Test
    void testFactoryCreaEstrategiaRentabilidad() {
        CalculoStrategy strategy = factory.crearStrategy("RENTABILIDAD",
                BigDecimal.valueOf(1000000), BigDecimal.valueOf(600000));

        assertNotNull(strategy);
        assertInstanceOf(CalculoRentabilidadStrategy.class, strategy);
        assertEquals("Margen de Rentabilidad", strategy.getNombre());
        assertEquals("%", strategy.getUnidad());
        assertEquals(BigDecimal.valueOf(40).setScale(2), strategy.calcular().setScale(2));
    }

    @Test
    void testFactoryTipoNoSoportado() {
        assertThrows(IllegalArgumentException.class, () ->
                factory.crearStrategy("OTRO", BigDecimal.ZERO, BigDecimal.ZERO));
    }
}
