package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.dto.AjustePrecioDTO;
import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AjustePrecioServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private MindicadorService mindicadorService;

    @InjectMocks
    private AjustePrecioService ajustePrecioService;

    @Test
    void testAjustarPreciosPorUf_whenUFHigher_shouldIncreasePrices() {
        Producto p = new Producto();
        p.setId(1);
        p.setPrecio(BigDecimal.valueOf(10000));
        p.setStock(10);
        when(productoRepository.findAll()).thenReturn(List.of(p));
        when(mindicadorService.obtenerUfActual()).thenReturn(BigDecimal.valueOf(37800));

        AjustePrecioDTO result = ajustePrecioService.ajustarPreciosPorUf();

        assertEquals(1, result.productosActualizados());
        assertEquals(0, BigDecimal.valueOf(1.05).compareTo(result.factorAjuste().setScale(2, RoundingMode.HALF_UP)));
        assertTrue(result.mensaje().contains("Precios ajustados"));
        verify(productoRepository).saveAll(any());
    }

    @Test
    void testAjustarPreciosPorUf_whenUFSame_shouldNotUpdatePrices() {
        Producto p = new Producto();
        p.setId(1);
        p.setPrecio(BigDecimal.valueOf(10000));
        p.setStock(10);
        when(productoRepository.findAll()).thenReturn(List.of(p));
        when(mindicadorService.obtenerUfActual()).thenReturn(BigDecimal.valueOf(36000));

        AjustePrecioDTO result = ajustePrecioService.ajustarPreciosPorUf();

        assertEquals(0, result.productosActualizados());
        assertEquals(0, BigDecimal.valueOf(1.0).compareTo(result.factorAjuste().setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    void testAjustarPreciosPorUf_whenMultipleProducts_shouldUpdateAll() {
        Producto p1 = new Producto();
        p1.setId(1);
        p1.setPrecio(BigDecimal.valueOf(10000));
        p1.setStock(10);
        Producto p2 = new Producto();
        p2.setId(2);
        p2.setPrecio(BigDecimal.valueOf(20000));
        p2.setStock(20);
        when(productoRepository.findAll()).thenReturn(List.of(p1, p2));
        when(mindicadorService.obtenerUfActual()).thenReturn(BigDecimal.valueOf(39600));

        AjustePrecioDTO result = ajustePrecioService.ajustarPreciosPorUf();

        assertEquals(2, result.productosActualizados());
        assertEquals(0, BigDecimal.valueOf(1.1).compareTo(result.factorAjuste().setScale(1, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    void testAjustarPreciosPorUf_whenNoProducts_shouldReturnZero() {
        when(productoRepository.findAll()).thenReturn(List.of());
        when(mindicadorService.obtenerUfActual()).thenReturn(BigDecimal.valueOf(36146.11));

        AjustePrecioDTO result = ajustePrecioService.ajustarPreciosPorUf();

        assertEquals(0, result.productosActualizados());
        verify(productoRepository, never()).saveAll(any());
    }
}
