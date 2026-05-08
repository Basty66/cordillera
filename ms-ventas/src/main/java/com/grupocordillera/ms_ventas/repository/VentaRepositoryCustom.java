package com.grupocordillera.ms_ventas.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface VentaRepositoryCustom {
    List<Map<String, Object>> generarReporteVentasPorSucursal();
    Map<String, Object> obtenerResumenVentas();
    BigDecimal calcularTotalVentasPorPeriodo(String inicio, String fin);
}
