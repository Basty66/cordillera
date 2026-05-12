package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.dto.ReporteVentasDTO;
import com.grupocordillera.ms_ventas.dto.ResumenVentasDTO;
import com.grupocordillera.ms_ventas.dto.TopProductoDTO;
import com.grupocordillera.ms_ventas.dto.VentaCategoriaDTO;
import com.grupocordillera.ms_ventas.dto.VentaMensualDTO;
import com.grupocordillera.ms_ventas.repository.VentaRepositoryCustom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private VentaRepositoryCustom ventaRepositoryCustom;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void testReporteVentasPorSucursal() {
        when(ventaRepositoryCustom.generarReporteVentasPorSucursal()).thenReturn(List.of(
                Map.of("sucursal", "Suc A", "ciudad", "Santiago", "totalVentas", 10L, "montoTotal", BigDecimal.valueOf(500000), "promedioVenta", BigDecimal.valueOf(50000))
        ));
        List<ReporteVentasDTO> result = reporteService.reporteVentasPorSucursal();
        assertEquals(1, result.size());
        assertEquals("Suc A", result.get(0).sucursal());
    }

    @Test
    void testResumenVentas() {
        when(ventaRepositoryCustom.obtenerResumenVentas()).thenReturn(
                Map.of("totalVentas", 10L, "montoTotal", BigDecimal.valueOf(500000), "promedioVenta", 50000.0)
        );
        ResumenVentasDTO result = reporteService.resumenVentas();
        assertEquals(10, result.totalVentas());
        assertEquals(BigDecimal.valueOf(500000), result.montoTotal());
    }

    @Test
    void testCalcularVentasPorPeriodo() {
        when(ventaRepositoryCustom.calcularTotalVentasPorPeriodo("2026-01-01", "2026-01-31"))
                .thenReturn(BigDecimal.valueOf(100000));
        BigDecimal result = reporteService.calcularVentasPorPeriodo("2026-01-01", "2026-01-31");
        assertEquals(BigDecimal.valueOf(100000), result);
    }

    @Test
    void testVentasMensuales() {
        when(ventaRepositoryCustom.ventasMensuales()).thenReturn(List.of(
                new VentaMensualDTO("Enero", 2026, 10L, BigDecimal.valueOf(500000), BigDecimal.valueOf(50000))
        ));
        List<VentaMensualDTO> result = reporteService.ventasMensuales();
        assertEquals(1, result.size());
        assertEquals("Enero", result.get(0).mes());
    }

    @Test
    void testVentasPorCategoria() {
        when(ventaRepositoryCustom.ventasPorCategoria()).thenReturn(List.of(
                new VentaCategoriaDTO("Electro", 5L, BigDecimal.valueOf(300000))
        ));
        List<VentaCategoriaDTO> result = reporteService.ventasPorCategoria();
        assertEquals(1, result.size());
        assertEquals("Electro", result.get(0).categoria());
    }

    @Test
    void testTopProductos() {
        when(ventaRepositoryCustom.topProductos(5)).thenReturn(List.of(
                new TopProductoDTO(1, "Producto A", 20L, BigDecimal.valueOf(400000))
        ));
        List<TopProductoDTO> result = reporteService.topProductos(5);
        assertEquals(1, result.size());
        assertEquals("Producto A", result.get(0).nombre());
    }
}
