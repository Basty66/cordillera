package com.grupocordillera.datosorg.service;

import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.repository.EmpleadoRepository;
import com.grupocordillera.datosorg.service.factory.EmpleadoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoFactory empleadoFactory;

    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }

    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public String generarMasivos(int cantidad) {
        List<Empleado> empleados = empleadoFactory.crearEmpleadosMasivos(cantidad);
        empleadoRepository.saveAll(empleados);
        return "Se generaron " + cantidad + " empleados correctamente.";
    }
}
