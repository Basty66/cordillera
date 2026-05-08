package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public String generarProductosMasivos(int cantidad) {
        String[] nombres = {"Laptop", "Mouse", "Teclado", "Monitor", "Impresora", "Webcam", "Auriculares"};
        List<Producto> lista = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            Producto p = new Producto();
            p.setNombre(nombres[(int)(Math.random()*nombres.length)] + " " + (i+1));
            p.setDescripcion("Producto de alta calidad serie " + i);
            p.setPrecio(BigDecimal.valueOf(Math.random() * 1000));
            p.setStock((int)(Math.random() * 500));
            lista.add(p);
        }
        productoRepository.saveAll(lista);
        return "¡Se inyectaron " + cantidad + " productos con éxito!";
    }
}