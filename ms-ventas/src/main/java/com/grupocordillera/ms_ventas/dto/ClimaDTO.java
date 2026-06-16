package com.grupocordillera.ms_ventas.dto;

import java.math.BigDecimal;

public record ClimaDTO(
    String ciudad,
    String descripcion,
    String icono,
    BigDecimal temperatura,
    BigDecimal humedad,
    BigDecimal viento,
    String pais
) {}
