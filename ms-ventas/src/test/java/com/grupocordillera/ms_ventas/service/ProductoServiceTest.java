package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void testGenerarProductosMasivos() {
        when(productoRepository.saveAll(any())).thenReturn(null);

        String resultado = productoService.generarProductosMasivos(5);

        assertEquals("¡Se inyectaron 5 productos con éxito!", resultado);
        verify(productoRepository, times(1)).saveAll(any());
    }
}
