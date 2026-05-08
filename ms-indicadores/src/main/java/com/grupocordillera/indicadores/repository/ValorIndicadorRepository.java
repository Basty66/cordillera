package com.grupocordillera.indicadores.repository;

import com.grupocordillera.indicadores.entity.ValorIndicador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValorIndicadorRepository extends JpaRepository<ValorIndicador, Integer> {

    List<ValorIndicador> findByIndicadorIdOrderByPeriodoDesc(Integer indicadorId);

    @Query("SELECT v FROM ValorIndicador v WHERE v.indicador.id IN " +
           "(SELECT i.id FROM Indicador i WHERE i.categoria.id = :categoriaId) " +
           "ORDER BY v.periodo DESC")
    List<ValorIndicador> findByCategoriaId(Integer categoriaId);
}
