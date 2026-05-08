package com.grupocordillera.indicadores.service.factory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculoInventarioStrategy implements CalculoStrategy {

    private final BigDecimal valorInventarioActual;
    private final BigDecimal valorInventarioInicial;

    public CalculoInventarioStrategy(BigDecimal valorInventarioActual, BigDecimal valorInventarioInicial) {
        this.valorInventarioActual = valorInventarioActual;
        this.valorInventarioInicial = valorInventarioInicial;
    }

    @Override
    public BigDecimal calcular() {
        if (valorInventarioInicial.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return valorInventarioActual
                .divide(valorInventarioInicial, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public String getNombre() {
        return "Rotación de Inventario";
    }

    @Override
    public String getUnidad() {
        return "%";
    }
}
