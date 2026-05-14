package com.grupocordillera.bff.dto;

import java.math.BigDecimal;

public record TopProductoDTO(
        Integer productoId,
        String nombre,
        long totalVendido,
        BigDecimal montoTotal
) {}
