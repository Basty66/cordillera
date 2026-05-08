package com.grupocordillera.ms_ventas.event;

import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.entity.Venta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VentaEventListenerTest {

    @InjectMocks
    private VentaEventListener listener;

    @Test
    void testOnVentaRegistrada() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Test");

        Venta venta = new Venta();
        venta.setId(1);
        venta.setSucursal(sucursal);
        venta.setPrecioTotal(BigDecimal.valueOf(50000));

        VentaRegistradaEvent event = new VentaRegistradaEvent(venta);

        assertDoesNotThrow(() -> listener.onVentaRegistrada(event));
    }
}
