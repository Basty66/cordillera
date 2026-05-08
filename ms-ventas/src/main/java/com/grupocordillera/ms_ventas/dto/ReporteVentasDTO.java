package com.grupocordillera.ms_ventas.dto;

import java.math.BigDecimal;

public record ReporteVentasDTO(
        String sucursal,
        String ciudad,
        long totalVentas,
        BigDecimal montoTotal,
        BigDecimal promedioVenta
) {}
