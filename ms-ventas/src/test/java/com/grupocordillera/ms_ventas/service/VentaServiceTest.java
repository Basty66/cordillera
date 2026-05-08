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
}
