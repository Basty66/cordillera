package com.grupocordillera.bff.dto;

public record EmpleadoResumenDTO(
        Integer id,
        String nombreCompleto,
        String cargo,
        String departamento
) {}
