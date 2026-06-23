package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.AjustePrecioDTO;
import com.grupocordillera.ms_ventas.dto.IndicadorEconomicoDTO;
import com.grupocordillera.ms_ventas.service.AjustePrecioService;
import com.grupocordillera.ms_ventas.service.MindicadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EconomicoControllerTest {

    @Mock
    private MindicadorService mindicadorService;
    @Mock
    private AjustePrecioService ajustePrecioService;

    @InjectMocks
    private EconomicoController economicoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(economicoController).build();
    }

    @Test
    void testObtenerIndicadores() throws Exception {
        Map<String, IndicadorEconomicoDTO.IndicadorValor> indicadores = new LinkedHashMap<>();
        indicadores.put("UF", new IndicadorEconomicoDTO.IndicadorValor("Unidad de Fomento", "Pesos", BigDecimal.valueOf(36146.11)));
        indicadores.put("DOLAR", new IndicadorEconomicoDTO.IndicadorValor("Dólar Observado", "Pesos", BigDecimal.valueOf(920.50)));
        when(mindicadorService.obtenerIndicadores()).thenReturn(new IndicadorEconomicoDTO("2026-06-23", indicadores));

        mockMvc.perform(get("/api/economico/indicadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fecha").value("2026-06-23"))
                .andExpect(jsonPath("$.indicadores.UF.valor").value(36146.11))
                .andExpect(jsonPath("$.indicadores.DOLAR.valor").value(920.50));
    }

    @Test
    void testAjustarPreciosPorUf() throws Exception {
        when(ajustePrecioService.ajustarPreciosPorUf()).thenReturn(
            new AjustePrecioDTO("Precios ajustados según variación de la UF", 10, BigDecimal.valueOf(1.05), BigDecimal.valueOf(36000), BigDecimal.valueOf(37800))
        );

        mockMvc.perform(post("/api/economico/ajustar-precios-uf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Precios ajustados según variación de la UF"))
                .andExpect(jsonPath("$.productosActualizados").value(10))
                .andExpect(jsonPath("$.factorAjuste").value(1.05));
    }
}
