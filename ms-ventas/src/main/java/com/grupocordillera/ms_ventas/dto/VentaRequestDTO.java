package com.grupocordillera.ms_ventas.dto;

import java.util.List;

public record VentaRequestDTO(
        Integer sucursalId,
        Integer usuarioId,
        List<DetalleRequestDTO> detalles
) {}