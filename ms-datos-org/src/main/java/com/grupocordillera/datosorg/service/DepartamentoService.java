package com.grupocordillera.datosorg.service;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public List<Departamento> obtenerTodos() {
        return departamentoRepository.findAll();
    }

    public Departamento guardar(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }
}
