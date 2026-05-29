package com.grupocordillera.bff.service.client;

import com.grupocordillera.bff.dto.ResumenVentasDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaClientTest {

    @Mock
    private RestTemplate restTemplate;

    private VentaClient ventaClient;

    @BeforeEach
    void setUp() {
        ventaClient = new VentaClient(restTemplate);
    }

    @Test
    void testObtenerResumenVentas() {
        Map<String, Object> mockResponse = Map.of(
                "totalVentas", 100,
                "montoTotal", 500000.0,
                "promedioVenta", 5000.0
        );
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        ResumenVentasDTO result = ventaClient.obtenerResumenVentas();

        assertNotNull(result);
        assertEquals(100, result.totalVentas());
        assertEquals(0, BigDecimal.valueOf(500000).compareTo(result.montoTotal()));
    }

    @Test
    void testObtenerResumenVentas_NullResponse() {
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        ResumenVentasDTO result = ventaClient.obtenerResumenVentas();

        assertEquals(0, result.totalVentas());
        assertEquals(BigDecimal.ZERO, result.montoTotal());
    }

    @Test
    void testObtenerSucursales() {
        List<Map<String, Object>> mockSucursales = List.of(
                Map.of("id", 1, "nombre", "Sucursal Central")
        );
        ResponseEntity<List<Map<String, Object>>> responseEntity = ResponseEntity.ok(mockSucursales);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        List<Map<String, Object>> result = ventaClient.obtenerSucursales();

        assertEquals(1, result.size());
        assertEquals("Sucursal Central", result.get(0).get("nombre"));
    }

    @Test
    void testContarSucursales() {
        when(restTemplate.getForObject(anyString(), eq(Long.class))).thenReturn(5L);

        long count = ventaClient.contarSucursales();

        assertEquals(5L, count);
    }
}
