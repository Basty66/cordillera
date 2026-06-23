package com.grupocordillera.ms_ventas.config;

import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.repository.DetalleVentaRepository;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import com.grupocordillera.ms_ventas.repository.SucursalRepository;
import com.grupocordillera.ms_ventas.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void testRun_whenDataExists_shouldMigrateImagesAndSkipSeed() {
        when(productoRepository.count()).thenReturn(1L);
        Producto p = new Producto();
        p.setId(1);
        p.setImagenUrl("https://picsum.photos/seed/elec1/400/300");
        p.setPrecio(BigDecimal.TEN);
        p.setStock(10);
        when(productoRepository.findAll()).thenReturn(List.of(p));

        dataInitializer.run();

        ArgumentCaptor<List<Producto>> captor = ArgumentCaptor.forClass(List.class);
        verify(productoRepository).saveAll(captor.capture());
        assertEquals("/images/productos/producto-1.png", captor.getValue().get(0).getImagenUrl());
        verify(detalleVentaRepository, never()).deleteAllInBatch();
    }

    @Test
    void testRun_whenNoData_shouldSeed() {
        Producto dummyProduct = new Producto();
        dummyProduct.setPrecio(BigDecimal.valueOf(1000));
        dummyProduct.setStock(10);
        List<Producto> dummyProducts = List.of(dummyProduct);
        List<Sucursal> dummySucursales = List.of(new Sucursal());
        when(productoRepository.count()).thenReturn(0L);
        when(productoRepository.findAll()).thenReturn(dummyProducts);
        when(sucursalRepository.findAll()).thenReturn(dummySucursales);

        dataInitializer.run();

        verify(detalleVentaRepository).deleteAllInBatch();
        verify(ventaRepository).deleteAllInBatch();
        verify(productoRepository).deleteAllInBatch();
        verify(sucursalRepository).deleteAllInBatch();
        verify(productoRepository, atLeast(1)).saveAll(any());
        verify(sucursalRepository, atLeast(1)).saveAll(any());
    }

    @Test
    void testMigrarImagenesExistentes_whenProductHasPicsumUrl_shouldUpdate() {
        List<Producto> productos = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            Producto p = new Producto();
            p.setId(i);
            p.setImagenUrl("https://picsum.photos/seed/test" + i + "/400/300");
            p.setPrecio(BigDecimal.valueOf(1000));
            p.setStock(10);
            productos.add(p);
        }
        when(productoRepository.count()).thenReturn(1L);
        when(productoRepository.findAll()).thenReturn(productos);

        dataInitializer.run();

        ArgumentCaptor<List<Producto>> captor = ArgumentCaptor.forClass(List.class);
        verify(productoRepository).saveAll(captor.capture());
        List<Producto> saved = captor.getValue();
        assertEquals(60, saved.size());
        assertTrue(saved.stream().allMatch(p -> p.getImagenUrl().startsWith("/images/productos/")));
    }

    @Test
    void testMigrarImagenesExistentes_whenProductHasLocalUrl_shouldNotUpdate() {
        Producto p = new Producto();
        p.setId(1);
        p.setImagenUrl("/images/productos/producto-1.png");
        p.setPrecio(BigDecimal.valueOf(1000));
        p.setStock(10);
        when(productoRepository.count()).thenReturn(1L);
        when(productoRepository.findAll()).thenReturn(List.of(p));

        dataInitializer.run();

        verify(productoRepository, never()).saveAll(any());
    }

    @Test
    void testMigrarImagenesExistentes_whenProductIdGreaterThan60_shouldNotUpdate() {
        Producto p = new Producto();
        p.setId(61);
        p.setImagenUrl("https://picsum.photos/seed/external/400/300");
        p.setPrecio(BigDecimal.valueOf(1000));
        p.setStock(10);
        when(productoRepository.count()).thenReturn(1L);
        when(productoRepository.findAll()).thenReturn(List.of(p));

        dataInitializer.run();

        verify(productoRepository, never()).saveAll(any());
    }
}
