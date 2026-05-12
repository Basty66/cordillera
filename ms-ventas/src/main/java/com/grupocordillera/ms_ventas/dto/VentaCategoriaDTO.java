package com.grupocordillera.ms_ventas.dto;

import java.math.BigDecimal;

public record VentaCategoriaDTO(
        String categoria,
        long totalVendido,
        BigDecimal montoTotal
) {}
