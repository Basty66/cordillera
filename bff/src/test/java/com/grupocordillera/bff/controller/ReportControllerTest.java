package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.entity.Ticket;
import com.grupocordillera.bff.repository.TicketRepository;
import com.grupocordillera.bff.repository.UsuarioRepository;
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
class ReportControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String obtenerToken() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String res = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + mapper.readTree(res).get("token").asText();
    }

    @Test
    void testDashboardReport() throws Exception {
        String token = obtenerToken();
        mvc.perform(get("/api/reportes/dashboard")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTickets").isNumber())
                .andExpect(jsonPath("$.ticketsAbiertos").isNumber())
                .andExpect(jsonPath("$.ticketsEnProgreso").isNumber())
                .andExpect(jsonPath("$.ticketsResueltos").isNumber())
                .andExpect(jsonPath("$.ticketsCerrados").isNumber())
                .andExpect(jsonPath("$.totalUsuarios").isNumber())
                .andExpect(jsonPath("$.porPrioridad").exists());
    }

    @Test
    void testExportTickets() throws Exception {
        String token = obtenerToken();
        mvc.perform(get("/api/reportes/tickets")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
