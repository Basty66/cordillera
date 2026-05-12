package com.grupocordillera.ms_ventas.repository;

import com.grupocordillera.ms_ventas.dto.TopProductoDTO;
import com.grupocordillera.ms_ventas.dto.VentaCategoriaDTO;
import com.grupocordillera.ms_ventas.dto.VentaMensualDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface VentaRepositoryCustom {
    List<Map<String, Object>> generarReporteVentasPorSucursal();
    Map<String, Object> obtenerResumenVentas();
    BigDecimal calcularTotalVentasPorPeriodo(String inicio, String fin);
    List<VentaMensualDTO> ventasMensuales();
    List<VentaCategoriaDTO> ventasPorCategoria();
    List<TopProductoDTO> topProductos(int limite);
}
