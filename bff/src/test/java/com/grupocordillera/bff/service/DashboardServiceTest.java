package com.grupocordillera.bff.service;

import com.grupocordillera.bff.dto.DashboardDTO;
import com.grupocordillera.bff.dto.KpiResumenDTO;
import com.grupocordillera.bff.dto.ResumenVentasDTO;
import com.grupocordillera.bff.service.client.DatosOrgClient;
import com.grupocordillera.bff.service.client.IndicadorClient;
import com.grupocordillera.bff.service.client.VentaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private VentaClient ventaClient;
    @Mock
    private IndicadorClient indicadorClient;
    @Mock
    private DatosOrgClient datosOrgClient;

    private DashboardService dashboardService;
    private ThreadPoolTaskExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.initialize();
        dashboardService = new DashboardService(ventaClient, indicadorClient, datosOrgClient, executor);
    }

    @Test
    void testObtenerDashboard() {
        ResumenVentasDTO ventas = new ResumenVentasDTO(10, BigDecimal.valueOf(500000), BigDecimal.valueOf(50000));
        List<KpiResumenDTO> kpis = List.of(
                new KpiResumenDTO(1, "Ticket Promedio", "CLP", BigDecimal.valueOf(50000), "2026-05")
        );

        when(ventaClient.obtenerResumenVentas()).thenReturn(ventas);
        when(indicadorClient.obtenerIndicadores()).thenReturn(kpis);
        when(datosOrgClient.contarEmpleados()).thenReturn(2L);
        when(ventaClient.contarSucursales()).thenReturn(1L);

        DashboardDTO dashboard = dashboardService.obtenerDashboard();

        assertNotNull(dashboard);
        assertEquals(10, dashboard.ventas().totalVentas());
        assertEquals(1, dashboard.indicadores().size());
        assertEquals(2, dashboard.totalEmpleados());
        assertEquals(1, dashboard.totalSucursales());
    }

    @Test
    void testObtenerDashboard_ConFallbacks() {
        when(ventaClient.obtenerResumenVentas())
                .thenReturn(new ResumenVentasDTO(0, BigDecimal.ZERO, BigDecimal.ZERO));
        when(indicadorClient.obtenerIndicadores()).thenReturn(Collections.emptyList());
        when(datosOrgClient.contarEmpleados()).thenReturn(0L);
        when(ventaClient.contarSucursales()).thenReturn(0L);

        DashboardDTO dashboard = dashboardService.obtenerDashboard();

        assertNotNull(dashboard);
        assertEquals(0, dashboard.totalEmpleados());
        assertEquals(0, dashboard.totalSucursales());
        assertTrue(dashboard.indicadores().isEmpty());
    }
}
