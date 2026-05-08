package com.grupocordillera.indicadores.service.factory;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculoVentasStrategy implements CalculoStrategy {

    private final BigDecimal totalVentas;
    private final long numeroTransacciones;

    public CalculoVentasStrategy(BigDecimal totalVentas, long numeroTransacciones) {
        this.totalVentas = totalVentas;
        this.numeroTransacciones = numeroTransacciones;
    }

    @Override
    public BigDecimal calcular() {
        if (numeroTransacciones == 0) return BigDecimal.ZERO;
        return totalVentas.divide(BigDecimal.valueOf(numeroTransacciones), 2, RoundingMode.HALF_UP);
    }

    @Override
    public String getNombre() {
        return "Ticket Promedio";
    }

    @Override
    public String getUnidad() {
        return "CLP";
    }
}
