package com.grupocordillera.ms_ventas.dto;

import java.math.BigDecimal;

public record VentaMensualDTO(
        String mes,
        int anio,
        long totalVentas,
        BigDecimal montoTotal,
        BigDecimal promedioVenta
) {}
