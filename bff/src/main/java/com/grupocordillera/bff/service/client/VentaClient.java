package com.grupocordillera.bff.service.client;

import com.grupocordillera.bff.dto.ResumenVentasDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class VentaClient {

    private static final Logger log = LoggerFactory.getLogger(VentaClient.class);

    @Value("${ms-ventas.url:http://localhost:8081}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public VentaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "ventas-client", fallbackMethod = "ventasFallback")
    public ResumenVentasDTO obtenerResumenVentas() {
        try {
            Map<String, Object> resumen = restTemplate.getForObject(
                    baseUrl + "/api/reportes/resumen-ventas",
                    Map.class);

            if (resumen == null || resumen.isEmpty()) {
                return new ResumenVentasDTO(0, BigDecimal.ZERO, BigDecimal.ZERO);
            }

            Long total = resumen.get("totalVentas") != null
                    ? ((Number) resumen.get("totalVentas")).longValue() : 0L;
            BigDecimal montoTotal = resumen.get("montoTotal") != null
                    ? new BigDecimal(resumen.get("montoTotal").toString()) : BigDecimal.ZERO;
            BigDecimal promedio = resumen.get("promedioVenta") != null
                    ? new BigDecimal(resumen.get("promedioVenta").toString()) : BigDecimal.ZERO;

            return new ResumenVentasDTO(total, montoTotal, promedio);
        } catch (Exception e) {
            log.warn("Error al obtener resumen de ventas: {}", e.getMessage());
            throw e;
        }
    }

    @SuppressWarnings("unused")
    private ResumenVentasDTO ventasFallback(Throwable t) {
        log.warn("Circuit Breaker activado para ms-ventas: {}", t.getMessage());
        return new ResumenVentasDTO(0, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @CircuitBreaker(name = "ventas-client", fallbackMethod = "sucursalesFallback")
    public List<Map<String, Object>> obtenerSucursales() {
        List<Map<String, Object>> sucursales = restTemplate.exchange(
                baseUrl + "/api/sucursales",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        ).getBody();
        return sucursales != null ? sucursales : Collections.emptyList();
    }

    @SuppressWarnings("unused")
    private List<Map<String, Object>> sucursalesFallback(Throwable t) {
        log.warn("Circuit Breaker activado para sucursales: {}", t.getMessage());
        return Collections.emptyList();
    }
}
