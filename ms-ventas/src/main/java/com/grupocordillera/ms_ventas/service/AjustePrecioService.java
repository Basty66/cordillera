package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.dto.AjustePrecioDTO;
import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AjustePrecioService {

    private static final BigDecimal UF_REFERENCIA = BigDecimal.valueOf(36000);

    private final ProductoRepository productoRepository;
    private final MindicadorService mindicadorService;

    public AjustePrecioService(ProductoRepository productoRepository, MindicadorService mindicadorService) {
        this.productoRepository = productoRepository;
        this.mindicadorService = mindicadorService;
    }

    @CacheEvict(value = "productos", allEntries = true)
    @Transactional
    public AjustePrecioDTO ajustarPreciosPorUf() {
        BigDecimal ufActual = mindicadorService.obtenerUfActual();
        BigDecimal factor = ufActual.divide(UF_REFERENCIA, 6, RoundingMode.HALF_UP);

        List<Producto> productos = productoRepository.findAll();
        int actualizados = 0;

        for (Producto p : productos) {
            BigDecimal nuevoPrecio = p.getPrecio().multiply(factor)
                    .setScale(0, RoundingMode.HALF_UP);
            if (nuevoPrecio.compareTo(p.getPrecio()) != 0) {
                p.setPrecio(nuevoPrecio);
                actualizados++;
            }
        }

        if (!productos.isEmpty()) {
            productoRepository.saveAll(productos);
        }

        return new AjustePrecioDTO(
            "Precios ajustados según variación de la UF",
            actualizados, factor, UF_REFERENCIA, ufActual
        );
    }
}
