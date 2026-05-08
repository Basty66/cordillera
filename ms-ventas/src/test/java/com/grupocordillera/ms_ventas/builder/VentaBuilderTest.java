package com.grupocordillera.ms_ventas.builder;

import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.entity.Sucursal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VentaBuilderTest {

    @Test
    void testBuildVentaCompleta() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Centro");

        Producto p1 = new Producto();
        p1.setId(1);
        p1.setNombre("Producto A");
        p1.setPrecio(BigDecimal.valueOf(1000));

        Producto p2 = new Producto();
        p2.setId(2);
        p2.setNombre("Producto B");
        p2.setPrecio(BigDecimal.valueOf(2000));

        var venta = new VentaBuilder()
                .sucursal(sucursal)
                .usuarioId(1)
                .agregarDetalle(p1, 3, p1.getPrecio())
                .agregarDetalle(p2, 2, p2.getPrecio())
                .build();

        assertNotNull(venta);
        assertEquals(sucursal, venta.getSucursal());
        assertEquals(Integer.valueOf(1), venta.getUsuarioId());
        assertEquals(2, venta.getDetalles().size());
        assertEquals(BigDecimal.valueOf(7000), venta.getPrecioTotal());
    }

    @Test
    void testBuildVentaSinSucursalError() {
        var builder = new VentaBuilder().usuarioId(1);
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testBuildVentaSinUsuarioError() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        var builder = new VentaBuilder().sucursal(sucursal);
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void testBuildVentaSinDetallesError() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        var builder = new VentaBuilder().sucursal(sucursal).usuarioId(1);
        assertThrows(IllegalStateException.class, builder::build);
    }
}
