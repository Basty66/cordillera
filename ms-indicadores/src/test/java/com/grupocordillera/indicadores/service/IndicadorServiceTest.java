package com.grupocordillera.indicadores.service;

import com.grupocordillera.indicadores.entity.CategoriaIndicador;
import com.grupocordillera.indicadores.entity.Indicador;
import com.grupocordillera.indicadores.entity.ValorIndicador;
import com.grupocordillera.indicadores.repository.CategoriaIndicadorRepository;
import com.grupocordillera.indicadores.repository.IndicadorRepository;
import com.grupocordillera.indicadores.repository.ValorIndicadorRepository;
import com.grupocordillera.indicadores.service.factory.CalculoIndicadorFactory;
import com.grupocordillera.indicadores.service.factory.CalculoVentasStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorServiceTest {

    @Mock
    private IndicadorRepository indicadorRepository;
    @Mock
    private ValorIndicadorRepository valorIndicadorRepository;
    @Mock
    private CategoriaIndicadorRepository categoriaIndicadorRepository;
    @Mock
    private CalculoIndicadorFactory calculoFactory;

    @InjectMocks
    private IndicadorService indicadorService;

    @Test
    void testCalcularValorIndicador() {
        Indicador indicador = new Indicador();
        indicador.setId(1);
        indicador.setNombre("Ticket Promedio");

        when(indicadorRepository.findById(1)).thenReturn(Optional.of(indicador));
        when(calculoFactory.crearStrategy(eq("VENTAS"), any(), any()))
                .thenReturn(new CalculoVentasStrategy(BigDecimal.valueOf(1000), 5L));
        when(valorIndicadorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ValorIndicador valor = indicadorService.calcularValorIndicador(1, "VENTAS",
                BigDecimal.valueOf(1000), BigDecimal.valueOf(5));

        assertNotNull(valor);
        assertEquals(0, BigDecimal.valueOf(200).setScale(2).compareTo(valor.getValor()));
        assertEquals(indicador, valor.getIndicador());
    }

    @Test
    void testGenerarIndicadoresPorDefecto_CuandoYaExisten() {
        when(categoriaIndicadorRepository.count()).thenReturn(1L);

        String resultado = indicadorService.generarIndicadoresPorDefecto();

        assertEquals("Los indicadores ya fueron inicializados.", resultado);
        verify(categoriaIndicadorRepository, never()).save(any());
    }

    @Test
    void testGenerarIndicadoresPorDefecto_CuandoNoExisten() {
        when(categoriaIndicadorRepository.count()).thenReturn(0L);
        when(categoriaIndicadorRepository.save(any())).thenAnswer(invocation -> {
            CategoriaIndicador c = invocation.getArgument(0);
            c.setId(1);
            return c;
        });
        when(indicadorRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String resultado = indicadorService.generarIndicadoresPorDefecto();

        assertTrue(resultado.contains("inicializados correctamente"));
        verify(categoriaIndicadorRepository, times(3)).save(any());
        verify(indicadorRepository, times(3)).save(any());
    }
}
