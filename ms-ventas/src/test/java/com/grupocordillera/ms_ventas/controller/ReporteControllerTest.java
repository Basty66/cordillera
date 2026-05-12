package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.ReporteVentasDTO;
import com.grupocordillera.ms_ventas.dto.ResumenVentasDTO;
import com.grupocordillera.ms_ventas.dto.TopProductoDTO;
import com.grupocordillera.ms_ventas.dto.VentaCategoriaDTO;
import com.grupocordillera.ms_ventas.dto.VentaMensualDTO;
import com.grupocordillera.ms_ventas.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private ReporteService reporteService;

    @InjectMocks
    private ReporteController reporteController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(reporteController).build();
    }

    @Test
    void testReporteVentasPorSucursal() throws Exception {
        when(reporteService.reporteVentasPorSucursal()).thenReturn(List.of(
                new ReporteVentasDTO("Suc A", "Santiago", 10L, BigDecimal.valueOf(500000), BigDecimal.valueOf(50000))
        ));

        mockMvc.perform(get("/api/reportes/ventas-por-sucursal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sucursal").value("Suc A"));
    }

    @Test
    void testResumenVentas() throws Exception {
        when(reporteService.resumenVentas()).thenReturn(
                new ResumenVentasDTO(10, BigDecimal.valueOf(500000), BigDecimal.valueOf(50000))
        );

        mockMvc.perform(get("/api/reportes/resumen-ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVentas").value(10));
    }

    @Test
    void testVentasPorPeriodo() throws Exception {
        when(reporteService.calcularVentasPorPeriodo("2026-01-01", "2026-01-31"))
                .thenReturn(BigDecimal.valueOf(100000));

        mockMvc.perform(get("/api/reportes/ventas-periodo")
                        .param("inicio", "2026-01-01")
                        .param("fin", "2026-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    void testVentasPorPeriodo_Null() throws Exception {
        when(reporteService.calcularVentasPorPeriodo("2026-01-01", "2026-01-31"))
                .thenReturn(null);

        mockMvc.perform(get("/api/reportes/ventas-periodo")
                        .param("inicio", "2026-01-01")
                        .param("fin", "2026-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    void testVentasMensuales() throws Exception {
        when(reporteService.ventasMensuales()).thenReturn(List.of(
                new VentaMensualDTO("Enero", 2026, 10L, BigDecimal.valueOf(500000), BigDecimal.valueOf(50000))
        ));

        mockMvc.perform(get("/api/reportes/ventas-mensuales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mes").value("Enero"));
    }

    @Test
    void testVentasPorCategoria() throws Exception {
        when(reporteService.ventasPorCategoria()).thenReturn(List.of(
                new VentaCategoriaDTO("Electro", 5L, BigDecimal.valueOf(300000))
        ));

        mockMvc.perform(get("/api/reportes/ventas-por-categoria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("Electro"));
    }

    @Test
    void testTopProductos() throws Exception {
        when(reporteService.topProductos(10)).thenReturn(List.of(
                new TopProductoDTO(1, "Producto A", 20L, BigDecimal.valueOf(400000))
        ));

        mockMvc.perform(get("/api/reportes/top-productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Producto A"));
    }

    @Test
    void testTopProductosConLimite() throws Exception {
        when(reporteService.topProductos(5)).thenReturn(List.of(
                new TopProductoDTO(1, "Producto A", 20L, BigDecimal.valueOf(400000))
        ));

        mockMvc.perform(get("/api/reportes/top-productos")
                        .param("limite", "5"))
                .andExpect(status().isOk());
    }
}
