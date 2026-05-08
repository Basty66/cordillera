package com.grupocordillera.ms_ventas.dto;

public record DetalleRequestDTO(
        Integer productoId,
        Integer cantidad
) {}