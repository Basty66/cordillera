package com.grupocordillera.ms_ventas.event;

import com.grupocordillera.ms_ventas.entity.DetalleVenta;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StockUpdateListener {

    private static final Logger log = LoggerFactory.getLogger(StockUpdateListener.class);
    private final ProductoRepository productoRepository;

    public StockUpdateListener(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @EventListener
    public void onVentaRegistrada(VentaRegistradaEvent event) {
        for (DetalleVenta detalle : event.getVenta().getDetalles()) {
            var producto = detalle.getProducto();
            log.info("Stock actualizado: Producto='{}', Stock restante={}",
                    producto.getNombre(), producto.getStock());
        }
    }
}
