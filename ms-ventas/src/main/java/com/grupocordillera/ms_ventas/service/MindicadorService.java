package com.grupocordillera.ms_ventas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupocordillera.ms_ventas.dto.IndicadorEconomicoDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MindicadorService {

    /** EXTERNAL API — mindicador.cl (https://mindicador.cl) */
    private static final String API_URL = "https://mindicador.cl/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MindicadorService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Cacheable("indicadoresEconomicos")
    public IndicadorEconomicoDTO obtenerIndicadores() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return fallback();
            }

            JsonNode root = objectMapper.readTree(response.body());
            String fecha = root.get("fecha").asText();
            Map<String, IndicadorEconomicoDTO.IndicadorValor> indicadores = new LinkedHashMap<>();

            String[] claves = {"uf", "dolar", "utm", "ipc", "euro"};
            for (String clave : claves) {
                JsonNode node = root.get(clave);
                if (node != null) {
                    indicadores.put(clave.toUpperCase(), new IndicadorEconomicoDTO.IndicadorValor(
                        node.get("nombre").asText(),
                        node.has("unidad_medida") ? node.get("unidad_medida").asText() : "Pesos",
                        BigDecimal.valueOf(node.get("valor").asDouble())
                    ));
                }
            }
            return new IndicadorEconomicoDTO(fecha, indicadores);
        } catch (Exception e) {
            return fallback();
        }
    }

    private IndicadorEconomicoDTO fallback() {
        Map<String, IndicadorEconomicoDTO.IndicadorValor> indicadores = new LinkedHashMap<>();
        indicadores.put("UF", new IndicadorEconomicoDTO.IndicadorValor("Unidad de Fomento", "Pesos", BigDecimal.valueOf(36146.11)));
        indicadores.put("DOLAR", new IndicadorEconomicoDTO.IndicadorValor("Dólar Observado", "Pesos", BigDecimal.valueOf(920.50)));
        indicadores.put("UTM", new IndicadorEconomicoDTO.IndicadorValor("Unidad Tributaria Mensual", "Pesos", BigDecimal.valueOf(63274)));
        indicadores.put("IPC", new IndicadorEconomicoDTO.IndicadorValor("IPC", "Porcentaje", BigDecimal.valueOf(-0.5)));
        return new IndicadorEconomicoDTO("Simulado", indicadores);
    }

    @CacheEvict(value = "indicadoresEconomicos", allEntries = true)
    public void limpiarCache() {}

    public BigDecimal obtenerUfActual() {
        var indicadores = obtenerIndicadores();
        var uf = indicadores.indicadores().get("UF");
        return uf != null ? uf.valor() : BigDecimal.valueOf(36146.11);
    }
}
