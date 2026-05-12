package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.dto.ReporteVentasDTO;
import com.grupocordillera.ms_ventas.dto.ResumenVentasDTO;
import com.grupocordillera.ms_ventas.dto.TopProductoDTO;
import com.grupocordillera.ms_ventas.dto.VentaCategoriaDTO;
import com.grupocordillera.ms_ventas.dto.VentaMensualDTO;
import com.grupocordillera.ms_ventas.repository.VentaRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VentaRepositoryCustom ventaRepositoryCustom;

    public List<ReporteVentasDTO> reporteVentasPorSucursal() {
        List<Map<String, Object>> datos = ventaRepositoryCustom.generarReporteVentasPorSucursal();
        return datos.stream()
                .map(m -> new ReporteVentasDTO(
                        (String) m.get("sucursal"),
                        (String) m.get("ciudad"),
                        (Long) m.get("totalVentas"),
                        (BigDecimal) m.get("montoTotal"),
                        (BigDecimal) m.get("promedioVenta")
                ))
                .collect(Collectors.toList());
    }

    public ResumenVentasDTO resumenVentas() {
        Map<String, Object> resumen = ventaRepositoryCustom.obtenerResumenVentas();
        return new ResumenVentasDTO(
                (Long) resumen.get("totalVentas"),
                (BigDecimal) resumen.get("montoTotal"),
                (BigDecimal) resumen.get("promedioVenta")
        );
    }

    public BigDecimal calcularVentasPorPeriodo(String inicio, String fin) {
        return ventaRepositoryCustom.calcularTotalVentasPorPeriodo(inicio, fin);
    }

    public List<VentaMensualDTO> ventasMensuales() {
        return ventaRepositoryCustom.ventasMensuales();
    }

    public List<VentaCategoriaDTO> ventasPorCategoria() {
        return ventaRepositoryCustom.ventasPorCategoria();
    }

    public List<TopProductoDTO> topProductos(int limite) {
        return ventaRepositoryCustom.topProductos(limite);
    }
}
