package com.grupocordillera.bff.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DatosOrgClient {

    private static final Logger log = LoggerFactory.getLogger(DatosOrgClient.class);

    @Value("${ms-datos-org.url:http://localhost:8082}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public DatosOrgClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "datos-org-client", fallbackMethod = "contarFallback")
    public long contarEmpleados() {
        Long count = restTemplate.getForObject(baseUrl + "/api/empleados/count", Long.class);
        return count != null ? count : 0L;
    }

    @SuppressWarnings("unused")
    private long contarFallback(Throwable t) {
        log.warn("Circuit Breaker activado para ms-datos-org: {}", t.getMessage());
        return 0L;
    }
}
