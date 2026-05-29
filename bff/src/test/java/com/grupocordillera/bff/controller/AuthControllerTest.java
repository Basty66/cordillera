package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.LoginRequest;
import com.grupocordillera.bff.dto.UsuarioRequest;
import com.grupocordillera.bff.entity.Usuario;
import com.grupocordillera.bff.repository.UsuarioRepository;
import com.grupocordillera.bff.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testHealth() throws Exception {
        mvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("bff"));
    }

    @Test
    void testLoginExitoso() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.rol").isString());
    }

    @Test
    void testLoginFallido() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"wrongpass\"}";
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginUsuarioInexistente() throws Exception {
        String loginJson = "{\"username\":\"noexiste\",\"password\":\"test123\"}";
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized());
    }
}
