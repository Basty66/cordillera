package com.grupocordillera.ms_ventas.dto;

import java.math.BigDecimal;

public record ResumenVentasDTO(
        long totalVentas,
        BigDecimal montoTotal,
        BigDecimal promedioVenta
) {}
