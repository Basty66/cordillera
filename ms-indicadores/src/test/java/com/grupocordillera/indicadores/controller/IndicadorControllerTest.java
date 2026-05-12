package com.grupocordillera.indicadores.controller;

import com.grupocordillera.indicadores.entity.CategoriaIndicador;
import com.grupocordillera.indicadores.entity.Indicador;
import com.grupocordillera.indicadores.entity.ValorIndicador;
import com.grupocordillera.indicadores.service.IndicadorService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class IndicadorControllerTest {

    @Mock
    private IndicadorService indicadorService;

    @InjectMocks
    private IndicadorController indicadorController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(indicadorController).build();
    }

    @Test
    void testListar() throws Exception {
        when(indicadorService.obtenerTodos()).thenReturn(List.of(new Indicador(), new Indicador()));

        mockMvc.perform(get("/api/indicadores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testCrear() throws Exception {
        Indicador indicador = new Indicador();
        indicador.setNombre("KPI Test");
        when(indicadorService.guardar(any())).thenReturn(indicador);

        mockMvc.perform(post("/api/indicadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"KPI Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("KPI Test"));
    }

    @Test
    void testListarCategorias() throws Exception {
        when(indicadorService.obtenerCategorias()).thenReturn(List.of(new CategoriaIndicador()));

        mockMvc.perform(get("/api/indicadores/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testCrearCategoria() throws Exception {
        CategoriaIndicador categoria = new CategoriaIndicador();
        categoria.setNombre("Categoria Test");
        when(indicadorService.guardarCategoria(any())).thenReturn(categoria);

        mockMvc.perform(post("/api/indicadores/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Categoria Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Categoria Test"));
    }

    @Test
    void testValoresActuales() throws Exception {
        when(indicadorService.obtenerValoresActuales()).thenReturn(List.of(new ValorIndicador()));

        mockMvc.perform(get("/api/indicadores/valores/actuales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testCalcular() throws Exception {
        ValorIndicador valor = new ValorIndicador();
        valor.setValor(BigDecimal.valueOf(50000));
        when(indicadorService.calcularValorIndicador(anyInt(), anyString(), any(), any()))
                .thenReturn(valor);

        mockMvc.perform(post("/api/indicadores/calcular")
                        .param("indicadorId", "1")
                        .param("tipo", "VENTAS")
                        .param("param1", "100000")
                        .param("param2", "50000"))
                .andExpect(status().isOk());
    }

    @Test
    void testCalcularSinParam2() throws Exception {
        ValorIndicador valor = new ValorIndicador();
        valor.setValor(BigDecimal.valueOf(100000));
        when(indicadorService.calcularValorIndicador(anyInt(), anyString(), any(), eq(BigDecimal.ZERO)))
                .thenReturn(valor);

        mockMvc.perform(post("/api/indicadores/calcular")
                        .param("indicadorId", "1")
                        .param("tipo", "VENTAS")
                        .param("param1", "100000"))
                .andExpect(status().isOk());
    }

    @Test
    void testInicializar() throws Exception {
        when(indicadorService.generarIndicadoresPorDefecto()).thenReturn("Indicadores generados");

        mockMvc.perform(post("/api/indicadores/inicializar"))
                .andExpect(status().isOk());
    }
}
