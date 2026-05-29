package com.grupocordillera.bff.service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatosOrgClientTest {

    @Mock
    private RestTemplate restTemplate;

    private DatosOrgClient datosOrgClient;

    @BeforeEach
    void setUp() {
        datosOrgClient = new DatosOrgClient(restTemplate);
    }

    @Test
    void testContarEmpleados() {
        when(restTemplate.getForObject(anyString(), eq(Long.class))).thenReturn(35L);

        long count = datosOrgClient.contarEmpleados();

        assertEquals(35L, count);
    }

    @Test
    void testContarEmpleados_NullResponse() {
        when(restTemplate.getForObject(anyString(), eq(Long.class))).thenReturn(null);

        long count = datosOrgClient.contarEmpleados();

        assertEquals(0L, count);
    }
}
