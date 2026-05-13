package com.grupocordillera.datosorg.controller;

import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.service.EmpleadoService;
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
class EmpleadoControllerTest {

    @Mock
    private EmpleadoService empleadoService;

    @InjectMocks
    private EmpleadoController empleadoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(empleadoController).build();
    }

    @Test
    void testListar() throws Exception {
        when(empleadoService.obtenerTodos()).thenReturn(List.of(new Empleado(), new Empleado()));

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testCrear() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setNombre("Juan");
        when(empleadoService.guardar(any())).thenReturn(empleado);

        mockMvc.perform(post("/api/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Juan\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void testGenerarMasivos() throws Exception {
        when(empleadoService.generarMasivos(5)).thenReturn("Empleados generados");

        mockMvc.perform(post("/api/empleados/generar/5"))
                .andExpect(status().isOk());
    }
}
