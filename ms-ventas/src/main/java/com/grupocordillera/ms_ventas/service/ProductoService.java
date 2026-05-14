package com.grupocordillera.ms_ventas.service;

import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Cacheable("productos")
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Cacheable(value = "productos", key = "#id")
    public Producto obtenerPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto con ID " + id + " no encontrado"));
    }

    @CacheEvict(value = "productos", allEntries = true)
    @Transactional
    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @CacheEvict(value = "productos", allEntries = true)
    @Transactional
    public Producto actualizarProducto(Integer id, Producto datos) {
        Producto existente = obtenerPorId(id);
        if (datos.getNombre() != null) existente.setNombre(datos.getNombre());
        if (datos.getDescripcion() != null) existente.setDescripcion(datos.getDescripcion());
        if (datos.getPrecio() != null) existente.setPrecio(datos.getPrecio());
        if (datos.getStock() != null) existente.setStock(datos.getStock());
        if (datos.getImagenUrl() != null) existente.setImagenUrl(datos.getImagenUrl());
        if (datos.getCategoria() != null) existente.setCategoria(datos.getCategoria());
        return productoRepository.save(existente);
    }

    @CacheEvict(value = "productos", allEntries = true)
    @Transactional
    public void eliminarProducto(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto con ID " + id + " no encontrado");
        }
        productoRepository.deleteById(id);
    }

    @CacheEvict(value = "productos", allEntries = true)
    public String generarProductosMasivos(int cantidad) {
        String[] nombres = {"Laptop", "Mouse", "Teclado", "Monitor", "Impresora", "Webcam", "Auriculares"};
        List<Producto> lista = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            String nombre = nombres[(int)(Math.random()*nombres.length)] + " " + (i+1);
            String categoria = nombre.split(" ")[0].toLowerCase();
            Producto p = new Producto();
            p.setNombre(nombre);
            p.setDescripcion("Producto de alta calidad serie " + i);
            p.setPrecio(BigDecimal.valueOf(Math.random() * 1000));
            p.setStock((int)(Math.random() * 500));
            p.setImagenUrl("https://picsum.photos/seed/" + categoria + (i+1) + "/400/300");
            lista.add(p);
        }
        productoRepository.saveAll(lista);
        return "Se inyectaron " + cantidad + " productos con exito!";
    }
}