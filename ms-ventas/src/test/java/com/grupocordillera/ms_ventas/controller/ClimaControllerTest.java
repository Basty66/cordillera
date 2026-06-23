package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.ClimaDTO;
import com.grupocordillera.ms_ventas.service.ClimaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClimaControllerTest {

    @Mock
    private ClimaService climaService;

    @InjectMocks
    private ClimaController climaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(climaController).build();
    }

    @Test
    void testObtenerClimaSucursales() throws Exception {
        List<ClimaDTO> lista = List.of(
            new ClimaDTO("Santiago", "Despejado", "01d", BigDecimal.valueOf(25), BigDecimal.valueOf(40), BigDecimal.valueOf(10), "CL"),
            new ClimaDTO("Valparaíso", "Nublado", "02d", BigDecimal.valueOf(18), BigDecimal.valueOf(60), BigDecimal.valueOf(15), "CL")
        );
        when(climaService.obtenerClimaSucursales()).thenReturn(lista);

        mockMvc.perform(get("/api/clima/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].ciudad").value("Santiago"))
                .andExpect(jsonPath("$[1].ciudad").value("Valparaíso"));
    }

    @Test
    void testObtenerClimaPorCiudad() throws Exception {
        ClimaDTO clima = new ClimaDTO("Santiago", "Despejado", "01d", BigDecimal.valueOf(25), BigDecimal.valueOf(40), BigDecimal.valueOf(10), "CL");
        when(climaService.obtenerClimaPorCiudad("Santiago")).thenReturn(clima);

        mockMvc.perform(get("/api/clima/ciudad/Santiago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciudad").value("Santiago"))
                .andExpect(jsonPath("$.descripcion").value("Despejado"))
                .andExpect(jsonPath("$.temperatura").value(25));
    }
}
