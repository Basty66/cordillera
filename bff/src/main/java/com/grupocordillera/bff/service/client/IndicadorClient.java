package com.grupocordillera.bff.service.client;

import com.grupocordillera.bff.dto.KpiResumenDTO;
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
public class IndicadorClient {

    private static final Logger log = LoggerFactory.getLogger(IndicadorClient.class);

    @Value("${ms-indicadores.url:http://localhost:8083}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public IndicadorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "indicadores-client", fallbackMethod = "indicadoresFallback")
    public List<KpiResumenDTO> obtenerIndicadores() {
        List<Map<String, Object>> indicadores = restTemplate.exchange(
                baseUrl + "/api/indicadores/valores/actuales",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        ).getBody();

        if (indicadores == null) return Collections.emptyList();

        return indicadores.stream()
                .map(m -> {
                    Map<String, Object> ind = (Map<String, Object>) m.get("indicador");
                    return new KpiResumenDTO(
                            ind != null ? (Integer) ind.get("id") : null,
                            ind != null ? (String) ind.get("nombre") : null,
                            ind != null ? (String) ind.get("unidad") : null,
                            new BigDecimal(m.get("valor").toString()),
                            (String) m.get("periodo")
                    );
                })
                .toList();
    }

    @SuppressWarnings("unused")
    private List<KpiResumenDTO> indicadoresFallback(Throwable t) {
        log.warn("Circuit Breaker activado para ms-indicadores: {}", t.getMessage());
        return Collections.emptyList();
    }
}
