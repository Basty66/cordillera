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
        List<Map<String, Object>> ventas = restTemplate.exchange(
                baseUrl + "/api/ventas",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        ).getBody();

        if (ventas == null || ventas.isEmpty()) {
            return new ResumenVentasDTO(0, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        long total = ventas.size();
        BigDecimal montoTotal = ventas.stream()
                .map(v -> new BigDecimal(v.get("montoTotal").toString()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal promedio = montoTotal.divide(BigDecimal.valueOf(total), 2, java.math.RoundingMode.HALF_UP);

        return new ResumenVentasDTO(total, montoTotal, promedio);
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
