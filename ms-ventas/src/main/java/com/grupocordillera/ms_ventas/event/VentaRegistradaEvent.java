package com.grupocordillera.ms_ventas.event;

import com.grupocordillera.ms_ventas.entity.Venta;

public class VentaRegistradaEvent {

    private final Venta venta;

    public VentaRegistradaEvent(Venta venta) {
        this.venta = venta;
    }

    public Venta getVenta() {
        return venta;
    }
}
