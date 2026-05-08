package com.grupocordillera.bff.service;

import com.grupocordillera.bff.dto.DashboardDTO;
import com.grupocordillera.bff.dto.KpiResumenDTO;
import com.grupocordillera.bff.dto.ResumenVentasDTO;
import com.grupocordillera.bff.service.client.DatosOrgClient;
import com.grupocordillera.bff.service.client.IndicadorClient;
import com.grupocordillera.bff.service.client.VentaClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final VentaClient ventaClient;
    private final IndicadorClient indicadorClient;
    private final DatosOrgClient datosOrgClient;

    public DashboardService(VentaClient ventaClient, IndicadorClient indicadorClient, DatosOrgClient datosOrgClient) {
        this.ventaClient = ventaClient;
        this.indicadorClient = indicadorClient;
        this.datosOrgClient = datosOrgClient;
    }

    public DashboardDTO obtenerDashboard() {
        ResumenVentasDTO ventas = ventaClient.obtenerResumenVentas();
        List<KpiResumenDTO> indicadores = indicadorClient.obtenerIndicadores();
        long totalEmpleados = datosOrgClient.obtenerEmpleados().size();
        long totalSucursales = ventaClient.obtenerSucursales().size();

        return new DashboardDTO(ventas, indicadores, totalEmpleados, totalSucursales);
    }
}
