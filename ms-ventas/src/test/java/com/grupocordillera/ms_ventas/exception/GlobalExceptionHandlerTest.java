package com.grupocordillera.ms_ventas.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleIllegalArgument() {
        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(
                new IllegalArgumentException("Stock insuficiente")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error de validación de negocio", response.getBody().get("error"));
        assertEquals("Stock insuficiente", response.getBody().get("message"));
    }

    @Test
    void testHandleRuntime() {
        ResponseEntity<Map<String, Object>> response = handler.handleRuntime(
                new RuntimeException("Sucursal no encontrada")
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso no encontrado", response.getBody().get("error"));
        assertEquals("Sucursal no encontrada", response.getBody().get("message"));
    }
}
