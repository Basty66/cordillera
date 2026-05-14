package com.grupocordillera.indicadores.repository;

import com.grupocordillera.indicadores.entity.Indicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndicadorRepository extends JpaRepository<Indicador, Integer> {
    @Query("SELECT i FROM Indicador i JOIN FETCH i.categoria")
    List<Indicador> findAllWithCategoria();

    List<Indicador> findByCategoriaId(Integer categoriaId);
}
