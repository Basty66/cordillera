package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.dto.DetalleRequestDTO;
import com.grupocordillera.ms_ventas.dto.VentaRequestDTO;
import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import com.grupocordillera.ms_ventas.repository.SucursalRepository;
import com.grupocordillera.ms_ventas.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void testRegistrarVenta_Success() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Test");

        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(1000));
        producto.setStock(10);

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        VentaRequestDTO request = new VentaRequestDTO(1, 1,
                List.of(new DetalleRequestDTO(1, 2)));

        var venta = ventaService.registrarVenta(request);

        assertNotNull(venta);
        assertEquals(1, venta.getDetalles().size());
        assertEquals(BigDecimal.valueOf(2000), venta.getPrecioTotal());
        assertEquals(8, producto.getStock()); // stock descontado
    }

    @Test
    void testRegistrarVenta_SinDetalles_Error() {
        VentaRequestDTO request = new VentaRequestDTO(1, 1, List.of());

        assertThrows(IllegalArgumentException.class, () -> ventaService.registrarVenta(request));
    }

    @Test
    void testRegistrarVenta_StockInsuficiente_Error() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);

        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Test");
        producto.setPrecio(BigDecimal.valueOf(500));
        producto.setStock(1);

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        VentaRequestDTO request = new VentaRequestDTO(1, 1,
                List.of(new DetalleRequestDTO(1, 5)));

        assertThrows(IllegalArgumentException.class, () -> ventaService.registrarVenta(request));
    }

    @Test
    void testObtenerTodas() {
        ventaService.obtenerTodas();
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void testRegistrarVenta_CantidadCero_Error() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Test");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));

        VentaRequestDTO request = new VentaRequestDTO(1, 1,
                List.of(new DetalleRequestDTO(1, 0)));

        assertThrows(IllegalArgumentException.class, () -> ventaService.registrarVenta(request));
    }

    @Test
    void testRegistrarVenta_SucursalInexistente_Error() {
        when(sucursalRepository.findById(99)).thenReturn(Optional.empty());

        VentaRequestDTO request = new VentaRequestDTO(99, 1,
                List.of(new DetalleRequestDTO(1, 2)));

        assertThrows(RuntimeException.class, () -> ventaService.registrarVenta(request));
    }

    @Test
    void testRegistrarVenta_ProductoInexistente_Error() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Test");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(productoRepository.findById(99)).thenReturn(Optional.empty());

        VentaRequestDTO request = new VentaRequestDTO(1, 1,
                List.of(new DetalleRequestDTO(99, 2)));

        assertThrows(RuntimeException.class, () -> ventaService.registrarVenta(request));
    }

    @Test
    void testRegistrarVenta_ConVariosDetalles_CalculaTotalCorrecto() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);

        Producto producto1 = new Producto();
        producto1.setId(1);
        producto1.setNombre("Producto A");
        producto1.setPrecio(BigDecimal.valueOf(1500));
        producto1.setStock(10);

        Producto producto2 = new Producto();
        producto2.setId(2);
        producto2.setNombre("Producto B");
        producto2.setPrecio(BigDecimal.valueOf(2500));
        producto2.setStock(5);

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto1));
        when(productoRepository.findById(2)).thenReturn(Optional.of(producto2));
        when(ventaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        VentaRequestDTO request = new VentaRequestDTO(1, 1,
                List.of(
                        new DetalleRequestDTO(1, 3),
                        new DetalleRequestDTO(2, 2)
                ));

        var venta = ventaService.registrarVenta(request);

        assertNotNull(venta);
        assertEquals(2, venta.getDetalles().size());
        // 3*1500 + 2*2500 = 4500 + 5000 = 9500
        assertEquals(BigDecimal.valueOf(9500), venta.getPrecioTotal());
        assertEquals(7, producto1.getStock()); // 10 - 3
        assertEquals(3, producto2.getStock()); // 5 - 2
    }

    @Test
    void testRegistrarVenta_UsuarioIdNoNulo() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);

        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(1000));
        producto.setStock(5);

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(ventaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        VentaRequestDTO request = new VentaRequestDTO(1, 42,
                List.of(new DetalleRequestDTO(1, 1)));

        var venta = ventaService.registrarVenta(request);

        assertEquals(42, venta.getUsuarioId());
    }
}
