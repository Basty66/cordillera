package com.grupocordillera.bff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DashboardDTO(
        ResumenVentasDTO ventas,
        List<KpiResumenDTO> indicadores,
        long totalEmpleados,
        long totalSucursales,
        List<VentaMensualDTO> ventasMensuales,
        List<VentaCategoriaDTO> ventasPorCategoria,
        List<TopProductoDTO> topProductos
) {}
