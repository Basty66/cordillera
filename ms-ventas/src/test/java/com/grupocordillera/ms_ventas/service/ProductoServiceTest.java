package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

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
    void testCrearProducto_DatosValidos_Correcto() {
        Producto producto = new Producto();
        producto.setNombre("Laptop Gamer");
        producto.setPrecio(BigDecimal.valueOf(1500));
        producto.setStock(10);

        when(productoRepository.save(any())).thenReturn(producto);

        var result = productoService.crearProducto(producto);

        assertEquals("Laptop Gamer", result.getNombre());
        assertEquals(BigDecimal.valueOf(1500), result.getPrecio());
        assertEquals(10, result.getStock());
    }

    @Test
    void testCrearProducto_SinNombre_Error() {
        Producto producto = new Producto();
        producto.setPrecio(BigDecimal.valueOf(100));
        producto.setStock(5);

        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testCrearProducto_NombreVacio_Error() {
        Producto producto = new Producto();
        producto.setNombre("   ");
        producto.setPrecio(BigDecimal.valueOf(100));
        producto.setStock(5);

        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testCrearProducto_SinPrecio_Error() {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setStock(5);

        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testCrearProducto_PrecioCero_Error() {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.ZERO);
        producto.setStock(5);

        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testCrearProducto_PrecioNegativo_Error() {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(-100));
        producto.setStock(5);

        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testCrearProducto_SinStock_Error() {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(100));

        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testCrearProducto_StockNegativo_Error() {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(100));
        producto.setStock(-1);

        assertThrows(IllegalArgumentException.class, () -> productoService.crearProducto(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void testActualizarProducto_PrecioNegativo_Error() {
        Producto existente = new Producto();
        existente.setId(1);
        existente.setNombre("Existente");
        existente.setPrecio(BigDecimal.valueOf(500));
        existente.setStock(10);

        when(productoRepository.findById(1)).thenReturn(Optional.of(existente));

        Producto datos = new Producto();
        datos.setPrecio(BigDecimal.valueOf(-50));

        assertThrows(IllegalArgumentException.class, () -> productoService.actualizarProducto(1, datos));
    }

    @Test
    void testActualizarProducto_StockNegativo_Error() {
        Producto existente = new Producto();
        existente.setId(1);
        existente.setNombre("Existente");
        existente.setPrecio(BigDecimal.valueOf(500));
        existente.setStock(10);

        when(productoRepository.findById(1)).thenReturn(Optional.of(existente));

        Producto datos = new Producto();
        datos.setStock(-5);

        assertThrows(IllegalArgumentException.class, () -> productoService.actualizarProducto(1, datos));
    }

    @Test
    void testGenerarProductosMasivos() {
        when(productoRepository.saveAll(any())).thenReturn(null);

        String resultado = productoService.generarProductosMasivos(5);

        assertEquals("Se inyectaron 5 productos con exito!", resultado);
        verify(productoRepository, times(1)).saveAll(any());
    }
}
