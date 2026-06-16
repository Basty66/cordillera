package com.grupocordillera.ms_ventas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupocordillera.ms_ventas.dto.ClimaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
public class ClimaService {

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s,CL&appid=%s&units=metric&lang=es";

    @Value("${openweather.api.key:}")
    private String apiKey;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ClimaService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<ClimaDTO> obtenerClimaSucursales() {
        List<String> ciudades = List.of("Santiago", "Valparaíso", "Concepción", "La Serena", "Antofagasta", "Temuco", "Rancagua", "Iquique", "Puerto Montt");
        return ciudades.stream().map(this::obtenerClimaPorCiudad).toList();
    }

    @Cacheable("clima")
    public ClimaDTO obtenerClimaPorCiudad(String ciudad) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("TU_API_KEY_AQUI")) {
            return generarClimaSimulado(ciudad);
        }
        try {
            String url = String.format(API_URL, java.net.URLEncoder.encode(ciudad, "UTF-8"), apiKey);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return generarClimaSimulado(ciudad);

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode main = root.get("main");
            JsonNode wind = root.get("wind");
            JsonNode weather = root.get("weather").get(0);

            return new ClimaDTO(
                root.get("name").asText(),
                weather.get("description").asText(),
                weather.get("icon").asText(),
                BigDecimal.valueOf(main.get("temp").asDouble()),
                BigDecimal.valueOf(main.get("humidity").asDouble()),
                BigDecimal.valueOf(wind.get("speed").asDouble()),
                root.has("sys") && root.get("sys").has("country") ? root.get("sys").get("country").asText() : "CL"
            );
        } catch (Exception e) {
            return generarClimaSimulado(ciudad);
        }
    }

    private ClimaDTO generarClimaSimulado(String ciudad) {
        String[] condiciones = {"Despejado", "Nublado", "Lluvia ligera", "Parcialmente nublado", "Cielo claro"};
        String[] iconos = {"01d", "02d", "10d", "03d", "01d"};
        int idx = Math.abs(ciudad.hashCode() % condiciones.length);
        return new ClimaDTO(
            ciudad, condiciones[idx], iconos[idx],
            BigDecimal.valueOf(15 + Math.abs(ciudad.hashCode() % 15)),
            BigDecimal.valueOf(40 + Math.abs(ciudad.hashCode() % 40)),
            BigDecimal.valueOf(5 + Math.abs(ciudad.hashCode() % 15)),
            "CL"
        );
    }
}
