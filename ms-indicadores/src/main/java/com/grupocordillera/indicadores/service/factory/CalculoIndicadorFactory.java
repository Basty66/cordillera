package com.grupocordillera.indicadores.service.factory;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CalculoIndicadorFactory {

    public CalculoStrategy crearStrategy(String tipo, Object... parametros) {
        return switch (tipo.toUpperCase()) {
            case "VENTAS" -> {
                BigDecimal totalVentas = (BigDecimal) parametros[0];
                long transacciones = ((BigDecimal) parametros[1]).longValue();
                yield new CalculoVentasStrategy(totalVentas, transacciones);
            }
            case "INVENTARIO" -> {
                BigDecimal valorActual = (BigDecimal) parametros[0];
                BigDecimal valorInicial = (BigDecimal) parametros[1];
                yield new CalculoInventarioStrategy(valorActual, valorInicial);
            }
            case "RENTABILIDAD" -> {
                BigDecimal ingresos = (BigDecimal) parametros[0];
                BigDecimal costos = (BigDecimal) parametros[1];
                yield new CalculoRentabilidadStrategy(ingresos, costos);
            }
            default -> throw new IllegalArgumentException("Tipo de cálculo no soportado: " + tipo);
        };
    }
}
