package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.dto.DetalleRequestDTO;
import com.grupocordillera.ms_ventas.dto.VentaRequestDTO;
import com.grupocordillera.ms_ventas.entity.*;
import com.grupocordillera.ms_ventas.event.VentaRegistradaEvent;
import com.grupocordillera.ms_ventas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final SucursalRepository sucursalRepository;
    private final ProductoRepository productoRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    @Transactional
    public Venta registrarVenta(VentaRequestDTO request) {
        // 1. Validaciones iniciales
        if (request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }

        // 2. Buscar Sucursal
        Sucursal sucursal = sucursalRepository.findById(request.sucursalId())
                .orElseThrow(() -> new RuntimeException("No existe la sucursal con ID: " + request.sucursalId()));

        // 3. Crear la Cabecera de la Venta
        Venta venta = new Venta();
        venta.setSucursal(sucursal);
        venta.setUsuarioId(request.usuarioId());

        BigDecimal totalVenta = BigDecimal.ZERO;

        // 4. Procesar cada detalle (producto)
        for (DetalleRequestDTO detalleReq : request.detalles()) {
            if (detalleReq.cantidad() == null || detalleReq.cantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad del producto ID " + detalleReq.productoId() + " debe ser mayor a cero.");
            }

            Producto producto = productoRepository.findById(detalleReq.productoId())
                    .orElseThrow(() -> new RuntimeException("No existe el producto con ID: " + detalleReq.productoId()));

            // Validar y descontar stock
            if (producto.getStock() < detalleReq.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para '" + producto.getNombre() + "'. Stock actual: " + producto.getStock());
            }
            producto.setStock(producto.getStock() - detalleReq.cantidad());
            productoRepository.save(producto);

            // Calcular subtotal
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalleReq.cantidad()));

            // Crear entidad DetalleVenta
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setProducto(producto);
            detalleVenta.setCantidad(detalleReq.cantidad());
            detalleVenta.setPrecioUnitario(producto.getPrecio());

            // Aquí asignamos el subtotal calculado en Java
            detalleVenta.setSubtotal(subtotal);

            // Agregar a la Venta y sumar al total global
            venta.agregarDetalle(detalleVenta);
            totalVenta = totalVenta.add(subtotal);
        }

        // 5. Asignar totales a la cabecera
        venta.setPrecioTotal(totalVenta);
        venta.setMontoTotal(totalVenta);

        // 6. Guardar todo (CascadeType.ALL guarda la Venta y todos sus Detalles automáticamente)
        Venta savedVenta = ventaRepository.save(venta);

        // 7. Publicar evento para los observers (listeners)
        eventPublisher.publishEvent(new VentaRegistradaEvent(savedVenta));

        return savedVenta;
    }
}