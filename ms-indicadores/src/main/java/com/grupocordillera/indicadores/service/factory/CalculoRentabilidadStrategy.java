package com.grupocordillera.indicadores.service.factory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculoRentabilidadStrategy implements CalculoStrategy {

    private final BigDecimal ingresos;
    private final BigDecimal costos;

    public CalculoRentabilidadStrategy(BigDecimal ingresos, BigDecimal costos) {
        this.ingresos = ingresos;
        this.costos = costos;
    }

    @Override
    public BigDecimal calcular() {
        if (ingresos.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return ingresos.subtract(costos)
                .divide(ingresos, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public String getNombre() {
        return "Margen de Rentabilidad";
    }

    @Override
    public String getUnidad() {
        return "%";
    }
}
