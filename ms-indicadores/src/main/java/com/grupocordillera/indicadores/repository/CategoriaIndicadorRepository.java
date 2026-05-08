package com.grupocordillera.indicadores.repository;

import com.grupocordillera.indicadores.entity.CategoriaIndicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaIndicadorRepository extends JpaRepository<CategoriaIndicador, Integer> {
}
