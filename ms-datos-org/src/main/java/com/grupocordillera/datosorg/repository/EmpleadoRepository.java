package com.grupocordillera.datosorg.repository;

import com.grupocordillera.datosorg.entity.Empleado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    @EntityGraph(attributePaths = {"departamento"})
    @Override
    List<Empleado> findAll();

    List<Empleado> findByDepartamentoId(Integer departamentoId);
    List<Empleado> findByActivoTrue();
    long countByActivoTrue();
}
