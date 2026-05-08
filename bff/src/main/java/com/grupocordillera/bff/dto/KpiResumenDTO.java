package com.grupocordillera.bff.dto;

import java.math.BigDecimal;

public record KpiResumenDTO(
        Integer id,
        String nombre,
        String unidad,
        BigDecimal valorActual,
        String periodo
) {}
