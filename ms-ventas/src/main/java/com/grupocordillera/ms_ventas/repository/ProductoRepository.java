package com.grupocordillera.ms_ventas.repository;

import com.grupocordillera.ms_ventas.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}