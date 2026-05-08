package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.TicketRequest;
import com.grupocordillera.bff.entity.Ticket;
import com.grupocordillera.bff.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        String loginJson = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        String res = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = "Bearer " + mapper.readTree(res).get("token").asText();
    }

    @Test
    @Order(1)
    void testListarTickets() throws Exception {
        mvc.perform(get("/api/tickets").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(2)
    void testCrearTicket() throws Exception {
        TicketRequest req = new TicketRequest();
        req.setTitulo("Test ticket from unit test");
        req.setDescripcion("This is a test ticket created during automated testing");
        req.setPrioridad(Ticket.Prioridad.ALTA);
        req.setCreadoPor("admin");

        String json = mapper.writeValueAsString(req);
        mvc.perform(post("/api/tickets")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Test ticket from unit test"))
                .andExpect(jsonPath("$.status").value("ABIERTO"))
                .andExpect(jsonPath("$.prioridad").value("ALTA"));
    }

    @Test
    @Order(3)
    void testActualizarStatus() throws Exception {
        String statusJson = "{\"status\":\"EN_PROGRESO\"}";
        mvc.perform(put("/api/tickets/1/status")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EN_PROGRESO"));
    }

    @Test
    @Order(4)
    void testReportesDashboard() throws Exception {
        mvc.perform(get("/api/reportes/dashboard").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTickets").isNumber())
                .andExpect(jsonPath("$.totalUsuarios").isNumber());
    }

    @Test
    @Order(5)
    void testAuthLoginFallido() throws Exception {
        String badLogin = "{\"username\":\"admin\",\"password\":\"wrongpass\"}";
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badLogin))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    void testAuthSinToken() throws Exception {
        mvc.perform(get("/api/tickets"))
                .andExpect(status().isForbidden());
    }
}
