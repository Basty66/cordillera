package com.grupocordillera.ms_ventas.dto;

import java.math.BigDecimal;
import java.util.Map;

public record IndicadorEconomicoDTO(
    String fecha,
    Map<String, IndicadorValor> indicadores
) {
    public record IndicadorValor(String nombre, String unidad, BigDecimal valor) {}
}
