package com.grupocordillera.ms_ventas.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class VentaEventListener {

    private static final Logger log = LoggerFactory.getLogger(VentaEventListener.class);

    @EventListener
    public void onVentaRegistrada(VentaRegistradaEvent event) {
        var venta = event.getVenta();
        log.info("Venta registrada: ID={}, Sucursal={}, Total={}, Productos={}",
                venta.getId(),
                venta.getSucursal().getNombre(),
                venta.getPrecioTotal(),
                venta.getDetalles().size());
    }
}
