package com.grupocordillera.bff.dto;

import java.math.BigDecimal;

public record ResumenVentasDTO(
        long totalVentas,
        BigDecimal montoTotal,
        BigDecimal promedioVenta
) {}
