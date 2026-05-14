package com.grupocordillera.ms_ventas.repository;

import com.grupocordillera.ms_ventas.entity.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    @EntityGraph(attributePaths = {"sucursal", "detalles", "detalles.producto"})
    List<Venta> findAll();

    @Query(value = "SELECT v FROM Venta v LEFT JOIN FETCH v.sucursal",
           countQuery = "SELECT COUNT(v) FROM Venta v")
    Page<Venta> findAll(Pageable pageable);
}