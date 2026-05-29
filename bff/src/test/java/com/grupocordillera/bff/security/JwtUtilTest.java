package com.grupocordillera.bff.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("miClaveSecretaQueDebeTenerAlMenos256BitsParaHMACSHA256", 3600000L);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("admin", "ADMIN", "Admin");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken("vendedor", "VENDEDOR", "Vendedor");
        Claims claims = jwtUtil.validateToken(token);
        assertNotNull(claims);
        assertEquals("vendedor", claims.getSubject());
    }

    @Test
    void testGetUsername() {
        String token = jwtUtil.generateToken("carla", "VENDEDOR", "Carla");
        assertEquals("carla", jwtUtil.getUsername(token));
    }

    @Test
    void testGetRol() {
        String token = jwtUtil.generateToken("admin", "ADMIN", "Admin");
        assertEquals("ADMIN", jwtUtil.getRol(token));
    }

    @Test
    void testTokenExpiradoLanzaExcepcion() {
        JwtUtil jwtUtilExpirado = new JwtUtil("miClaveSecretaQueDebeTenerAlMenos256BitsParaHMACSHA256", -1000L);
        String token = jwtUtilExpirado.generateToken("test", "TEST", "Test");
        assertThrows(Exception.class, () -> jwtUtilExpirado.validateToken(token));
    }
}
