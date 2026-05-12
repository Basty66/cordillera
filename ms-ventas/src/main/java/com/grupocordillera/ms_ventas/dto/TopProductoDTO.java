package com.grupocordillera.ms_ventas.dto;

import java.math.BigDecimal;

public record TopProductoDTO(
        Integer productoId,
        String nombre,
        long totalVendido,
        BigDecimal montoTotal
) {}
