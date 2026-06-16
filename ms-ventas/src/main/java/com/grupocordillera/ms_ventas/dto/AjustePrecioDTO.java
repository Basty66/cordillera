package com.grupocordillera.ms_ventas.dto;

import java.math.BigDecimal;

public record AjustePrecioDTO(
    String mensaje,
    int productosActualizados,
    BigDecimal factorAjuste,
    BigDecimal ufAnterior,
    BigDecimal ufActual
) {}
