package com.grupocordillera.bff.service;

import com.grupocordillera.bff.dto.DashboardDTO;
import com.grupocordillera.bff.dto.KpiResumenDTO;
import com.grupocordillera.bff.dto.ResumenVentasDTO;
import com.grupocordillera.bff.service.client.DatosOrgClient;
import com.grupocordillera.bff.service.client.IndicadorClient;
import com.grupocordillera.bff.service.client.VentaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final VentaClient ventaClient;
    private final IndicadorClient indicadorClient;
    private final DatosOrgClient datosOrgClient;

    public DashboardService(VentaClient ventaClient, IndicadorClient indicadorClient, DatosOrgClient datosOrgClient) {
        this.ventaClient = ventaClient;
        this.indicadorClient = indicadorClient;
        this.datosOrgClient = datosOrgClient;
    }

    public DashboardDTO obtenerDashboard() {
        ResumenVentasDTO ventas = obtenerVentasSeguro();
        List<KpiResumenDTO> indicadores = obtenerIndicadoresSeguro();
        long totalEmpleados = obtenerEmpleadosSeguro();
        long totalSucursales = obtenerSucursalesSeguro();

        return new DashboardDTO(ventas, indicadores, totalEmpleados, totalSucursales);
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
            return datosOrgClient.obtenerEmpleados().size();
        } catch (Exception e) {
            log.warn("Error al obtener empleados: {}", e.getMessage());
            return 0L;
        }
    }

    private long obtenerSucursalesSeguro() {
        try {
            return ventaClient.obtenerSucursales().size();
        } catch (Exception e) {
            log.warn("Error al obtener sucursales: {}", e.getMessage());
            return 0L;
        }
    }
}
