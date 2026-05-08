package com.grupocordillera.datosorg.service.factory;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.repository.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class EmpleadoFactory {

    private final DepartamentoRepository departamentoRepository;

    private static final String[] NOMBRES = {"Carlos", "María", "José", "Ana", "Luis", "Patricia", "Jorge", "Carmen", "Pedro", "Sofía"};
    private static final String[] APELLIDOS = {"González", "Muñoz", "Rojas", "Díaz", "Pérez", "Soto", "Contreras", "Silva", "Martínez", "Sepúlveda"};
    private static final String[] CARGOS = {"Analista", "Supervisor", "Coordinador", "Gerente", "Asistente", "Ejecutivo", "Consultor", "Desarrollador"};

    public Empleado crearEmpleado(String nombre, String apellido, String cargo, Departamento departamento) {
        Empleado emp = new Empleado();
        emp.setNombre(nombre);
        emp.setApellido(apellido);
        emp.setEmail((nombre + "." + apellido + "@grupocordillera.cl").toLowerCase());
        emp.setCargo(cargo);
        emp.setDepartamento(departamento);
        emp.setFechaContratacion(LocalDate.now().minusDays(ThreadLocalRandom.current().nextLong(1, 365 * 5)));
        emp.setActivo(true);
        return emp;
    }

    public List<Empleado> crearEmpleadosMasivos(int cantidad) {
        List<Departamento> departamentos = departamentoRepository.findAll();
        if (departamentos.isEmpty()) {
            Departamento defaultDept = new Departamento();
            defaultDept.setNombre("General");
            defaultDept.setDescripcion("Departamento por defecto");
            departamentos = List.of(departamentoRepository.save(defaultDept));
        }

        List<Empleado> empleados = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < cantidad; i++) {
            String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
            String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
            String cargo = CARGOS[random.nextInt(CARGOS.length)];
            Departamento dept = departamentos.get(random.nextInt(departamentos.size()));

            Empleado emp = crearEmpleado(nombre, apellido, cargo, dept);
            emp.setEmail((nombre + "." + apellido + i + "@grupocordillera.cl").toLowerCase());
            empleados.add(emp);
        }
        return empleados;
    }
}
