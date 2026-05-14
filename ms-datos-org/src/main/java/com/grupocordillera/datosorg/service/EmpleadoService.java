package com.grupocordillera.datosorg.service;

import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.repository.EmpleadoRepository;
import com.grupocordillera.datosorg.service.factory.EmpleadoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoFactory empleadoFactory;

    @Cacheable("empleados")
    @Transactional(readOnly = true)
    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }

    @Cacheable("empleadosCount")
    public long contarActivos() {
        return empleadoRepository.countByActivoTrue();
    }

    @CacheEvict(value = {"empleados", "empleadosCount"}, allEntries = true)
    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    @CacheEvict(value = {"empleados", "empleadosCount"}, allEntries = true)
    public String generarMasivos(int cantidad) {
        List<Empleado> empleados = empleadoFactory.crearEmpleadosMasivos(cantidad);
        empleadoRepository.saveAll(empleados);
        return "Se generaron " + cantidad + " empleados correctamente.";
    }
}
