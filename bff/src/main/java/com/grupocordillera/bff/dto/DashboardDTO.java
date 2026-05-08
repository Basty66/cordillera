package com.grupocordillera.bff.dto;

import java.util.List;

public record DashboardDTO(
        ResumenVentasDTO ventas,
        List<KpiResumenDTO> indicadores,
        long totalEmpleados,
        long totalSucursales
) {}
