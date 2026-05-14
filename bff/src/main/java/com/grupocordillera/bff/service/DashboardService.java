package com.grupocordillera.bff.service;

import com.grupocordillera.bff.dto.DashboardDTO;
import com.grupocordillera.bff.dto.KpiResumenDTO;
import com.grupocordillera.bff.dto.ResumenVentasDTO;
import com.grupocordillera.bff.dto.TopProductoDTO;
import com.grupocordillera.bff.dto.VentaCategoriaDTO;
import com.grupocordillera.bff.dto.VentaMensualDTO;
import com.grupocordillera.bff.service.client.DatosOrgClient;
import com.grupocordillera.bff.service.client.IndicadorClient;
import com.grupocordillera.bff.service.client.VentaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final VentaClient ventaClient;
    private final IndicadorClient indicadorClient;
    private final DatosOrgClient datosOrgClient;
    private final ThreadPoolTaskExecutor executor;

    public DashboardService(VentaClient ventaClient, IndicadorClient indicadorClient,
                            DatosOrgClient datosOrgClient, ThreadPoolTaskExecutor executor) {
        this.ventaClient = ventaClient;
        this.indicadorClient = indicadorClient;
        this.datosOrgClient = datosOrgClient;
        this.executor = executor;
    }

    @Cacheable("dashboard")
    public DashboardDTO obtenerDashboard() {
        CompletableFuture<ResumenVentasDTO> ventasFuture =
                CompletableFuture.supplyAsync(this::obtenerVentasSeguro, executor);
        CompletableFuture<List<KpiResumenDTO>> indicadoresFuture =
                CompletableFuture.supplyAsync(this::obtenerIndicadoresSeguro, executor);
        CompletableFuture<Long> empleadosFuture =
                CompletableFuture.supplyAsync(this::obtenerEmpleadosSeguro, executor);
        CompletableFuture<Long> sucursalesFuture =
                CompletableFuture.supplyAsync(this::obtenerSucursalesSeguro, executor);
        CompletableFuture<List<VentaMensualDTO>> ventasMensualesFuture =
                CompletableFuture.supplyAsync(this::obtenerVentasMensualesSeguro, executor);
        CompletableFuture<List<VentaCategoriaDTO>> ventasCategoriaFuture =
                CompletableFuture.supplyAsync(this::obtenerVentasCategoriaSeguro, executor);
        CompletableFuture<List<TopProductoDTO>> topProductosFuture =
                CompletableFuture.supplyAsync(this::obtenerTopProductosSeguro, executor);

        CompletableFuture.allOf(ventasFuture, indicadoresFuture, empleadosFuture, sucursalesFuture,
                ventasMensualesFuture, ventasCategoriaFuture, topProductosFuture).join();

        return new DashboardDTO(
                ventasFuture.join(),
                indicadoresFuture.join(),
                empleadosFuture.join(),
                sucursalesFuture.join(),
                ventasMensualesFuture.join(),
                ventasCategoriaFuture.join(),
                topProductosFuture.join()
        );
    }

    private ResumenVentasDTO obtenerVentasSeguro() {
        try {
            return ventaClient.obtenerResumenVentas();
        } catch (Exception e) {
            log.warn("Error al obtener ventas: {}", e.getMessage());
            return new ResumenVentasDTO(0, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }

    private List<KpiResumenDTO> obtenerIndicadoresSeguro() {
        try {
            return indicadorClient.obtenerIndicadores();
        } catch (Exception e) {
            log.warn("Error al obtener indicadores: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private long obtenerEmpleadosSeguro() {
        try {
            return datosOrgClient.contarEmpleados();
        } catch (Exception e) {
            log.warn("Error al obtener empleados: {}", e.getMessage());
            return 0L;
        }
    }

    private long obtenerSucursalesSeguro() {
        try {
            return ventaClient.contarSucursales();
        } catch (Exception e) {
            log.warn("Error al obtener sucursales: {}", e.getMessage());
            return 0L;
        }
    }

    private List<VentaMensualDTO> obtenerVentasMensualesSeguro() {
        try {
            return ventaClient.obtenerVentasMensuales();
        } catch (Exception e) {
            log.warn("Error al obtener ventas mensuales: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<VentaCategoriaDTO> obtenerVentasCategoriaSeguro() {
        try {
            return ventaClient.obtenerVentasPorCategoria();
        } catch (Exception e) {
            log.warn("Error al obtener ventas por categoria: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<TopProductoDTO> obtenerTopProductosSeguro() {
        try {
            return ventaClient.obtenerTopProductos(10);
        } catch (Exception e) {
            log.warn("Error al obtener top productos: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
