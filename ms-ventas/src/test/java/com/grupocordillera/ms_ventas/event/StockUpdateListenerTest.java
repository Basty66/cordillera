package com.grupocordillera.ms_ventas.event;

import com.grupocordillera.ms_ventas.entity.DetalleVenta;
import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.entity.Venta;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class StockUpdateListenerTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private StockUpdateListener listener;

    @Test
    void testOnVentaRegistrada() {
        Producto producto = new Producto();
        producto.setId(1);
        producto.setNombre("Producto Test");
        producto.setStock(50);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(5);

        Venta venta = new Venta();
        venta.setId(1);
        venta.setDetalles(List.of(detalle));

        VentaRegistradaEvent event = new VentaRegistradaEvent(venta);

        assertDoesNotThrow(() -> listener.onVentaRegistrada(event));
    }

    @Test
    void testOnVentaRegistradaSinDetalles() {
        Venta venta = new Venta();
        venta.setId(2);
        venta.setDetalles(List.of());

        VentaRegistradaEvent event = new VentaRegistradaEvent(venta);

        assertDoesNotThrow(() -> listener.onVentaRegistrada(event));
    }
}
