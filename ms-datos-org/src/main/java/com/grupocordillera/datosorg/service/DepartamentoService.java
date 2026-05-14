package com.grupocordillera.datosorg.service;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    @Cacheable("departamentos")
    public List<Departamento> obtenerTodos() {
        return departamentoRepository.findAll();
    }

    @CacheEvict(value = "departamentos", allEntries = true)
    public Departamento guardar(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }
}
