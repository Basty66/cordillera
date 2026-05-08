package com.grupocordillera.ms_ventas.builder;

import com.grupocordillera.ms_ventas.entity.DetalleVenta;
import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.entity.Venta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VentaBuilder {

    private Sucursal sucursal;
    private Integer usuarioId;
    private BigDecimal precioTotal = BigDecimal.ZERO;
    private BigDecimal montoTotal = BigDecimal.ZERO;
    private final List<DetalleVenta> detalles = new ArrayList<>();

    public VentaBuilder sucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
        return this;
    }

    public VentaBuilder usuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
        return this;
    }

    public VentaBuilder agregarDetalle(Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precioUnitario);
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        detalle.setSubtotal(subtotal);
        this.detalles.add(detalle);
        this.precioTotal = this.precioTotal.add(subtotal);
        this.montoTotal = this.montoTotal.add(subtotal);
        return this;
    }

    public Venta build() {
        if (sucursal == null) {
            throw new IllegalStateException("La sucursal es obligatoria");
        }
        if (usuarioId == null) {
            throw new IllegalStateException("El usuario ID es obligatorio");
        }
        if (detalles.isEmpty()) {
            throw new IllegalStateException("La venta debe contener al menos un detalle");
        }
        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setUsuarioId(usuarioId);
        venta.setPrecioTotal(precioTotal);
        venta.setMontoTotal(montoTotal);
        for (DetalleVenta detalle : detalles) {
            venta.agregarDetalle(detalle);
        }
        return venta;
    }
}
