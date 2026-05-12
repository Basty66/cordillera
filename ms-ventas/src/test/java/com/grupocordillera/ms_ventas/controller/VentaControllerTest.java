package com.grupocordillera.ms_ventas.controller;

import com.grupocordillera.ms_ventas.dto.DetalleRequestDTO;
import com.grupocordillera.ms_ventas.dto.VentaRequestDTO;
import com.grupocordillera.ms_ventas.entity.Venta;
import com.grupocordillera.ms_ventas.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController).build();
    }

    @Test
    void testListarTodas() throws Exception {
        when(ventaService.obtenerTodas()).thenReturn(List.of(new Venta(), new Venta()));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testCrearVenta() throws Exception {
        Venta venta = new Venta();
        venta.setPrecioTotal(BigDecimal.valueOf(50000));
        when(ventaService.registrarVenta(any())).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idSucursal":1,"idVendedor":1,"detalles":[{"idProducto":1,"cantidad":2}]}
                                """))
                .andExpect(status().isCreated());
    }
}
