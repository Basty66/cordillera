package com.grupocordillera.bff.service.client;

import com.grupocordillera.bff.dto.KpiResumenDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorClientTest {

    @Mock
    private RestTemplate restTemplate;

    private IndicadorClient indicadorClient;

    @BeforeEach
    void setUp() {
        indicadorClient = new IndicadorClient(restTemplate);
    }

    @Test
    void testObtenerIndicadores() {
        List<Map<String, Object>> mockIndicadores = List.of(
                Map.of(
                        "indicador", Map.of("id", 1, "nombre", "Ticket Promedio", "unidad", "CLP"),
                        "valor", 50000,
                        "periodo", "2026-05"
                )
        );
        ResponseEntity<List<Map<String, Object>>> responseEntity = ResponseEntity.ok(mockIndicadores);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        List<KpiResumenDTO> result = indicadorClient.obtenerIndicadores();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ticket Promedio", result.get(0).nombre());
    }

    @Test
    void testObtenerIndicadores_NullResponse() {
        ResponseEntity<List<Map<String, Object>>> responseEntity = ResponseEntity.ok(null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        List<KpiResumenDTO> result = indicadorClient.obtenerIndicadores();

        assertTrue(result.isEmpty());
    }
}
