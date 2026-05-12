package com.grupocordillera.datosorg.controller;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.service.DepartamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepartamentoControllerTest {

    @Mock
    private DepartamentoService departamentoService;

    @InjectMocks
    private DepartamentoController departamentoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(departamentoController).build();
    }

    @Test
    void testListar() throws Exception {
        when(departamentoService.obtenerTodos()).thenReturn(List.of(new Departamento(), new Departamento()));

        mockMvc.perform(get("/api/departamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testCrear() throws Exception {
        Departamento depto = new Departamento();
        depto.setNombre("Ventas");
        when(departamentoService.guardar(any())).thenReturn(depto);

        mockMvc.perform(post("/api/departamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ventas\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ventas"));
    }
}
