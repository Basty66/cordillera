package com.grupocordillera.datosorg.config;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.repository.DepartamentoRepository;
import com.grupocordillera.datosorg.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;

    @Override
    public void run(String... args) {
        if (departamentoRepository.count() > 0) {
            log.info("Datos organizacionales ya existen — omitiendo");
            return;
        }
        log.info("=== INICIANDO CARGA DE DATOS ORGANIZACIONALES ===");

        List<Departamento> deptos = crearDepartamentos();
        departamentoRepository.saveAll(deptos);
        deptos = departamentoRepository.findAll();
        log.info("{} departamentos creados", deptos.size());

        List<Empleado> empleados = crearEmpleados(deptos);
        empleadoRepository.saveAll(empleados);
        log.info("{} empleados creados", empleados.size());
    }

    private List<Departamento> crearDepartamentos() {
        String[][] data = {
            {"Gerencia General", "Dirección y planificación estratégica"},
            {"Ventas", "Fuerza de ventas y atención al cliente"},
            {"Marketing", "Publicidad, redes sociales y branding"},
            {"Operaciones", "Logística, bodega y distribución"},
            {"Finanzas", "Contabilidad, tesorería y presupuestos"},
            {"TI", "Sistemas, infraestructura y desarrollo"},
            {"Recursos Humanos", "Selección, capacitación y bienestar"},
            {"Postventa", "Soporte técnico y servicio al cliente"},
        };
        List<Departamento> lista = new ArrayList<>();
        for (String[] d : data) {
            Departamento dept = new Departamento();
            dept.setNombre(d[0]);
            dept.setDescripcion(d[1]);
            lista.add(dept);
        }
        return lista;
    }

    private List<Empleado> crearEmpleados(List<Departamento> deptos) {
        String[][] data = {
            {"Ana", "López", "Gerente General", "0"},
            {"Carlos", "Muñoz", "Subgerente General", "0"},
            {"María", "González", "Jefa de Ventas", "1"},
            {"José", "Rojas", "Vendedor Senior", "1"},
            {"Patricia", "Martínez", "Vendedora", "1"},
            {"Luis", "Soto", "Vendedor", "1"},
            {"Carmen", "Díaz", "Vendedora", "1"},
            {"Pedro", "Pérez", "Vendedor", "1"},
            {"Sofía", "Silva", "Vendedora", "1"},
            {"Jorge", "Sepúlveda", "Analista Marketing", "2"},
            {"Laura", "Contreras", "Community Manager", "2"},
            {"Diego", "Castillo", "Diseñador Gráfico", "2"},
            {"Valentina", "Morales", "Jefa de Operaciones", "3"},
            {"Felipe", "Torres", "Bodeguero", "3"},
            {"Andrés", "Ramírez", "Bodeguero", "3"},
            {"Camila", "Flores", "Coordinadora Logística", "3"},
            {"Rodrigo", "Vargas", "Analista Financiero", "4"},
            {"Daniela", "Cruz", "Contadora", "4"},
            {"Fernando", "Reyes", "Tesorer", "4"},
            {"Claudia", "Peña", "Jefa de TI", "5"},
            {"Mauricio", "Cárdenas", "Desarrollador Full Stack", "5"},
            {"Gabriela", "Vega", "Desarrolladora Frontend", "5"},
            {"Sebastián", "Bravo", "Desarrollador Backend", "5"},
            {"Catalina", "Guerrero", "DBA", "5"},
            {"Francisco", "Navarro", "Soporte TI", "5"},
            {"Paulina", "Herrera", "Jefa de RRHH", "6"},
            {"Nicolás", "Medina", "Reclutador", "6"},
            {"Marisol", "Acosta", "Asistente RRHH", "6"},
            {"Alejandro", "Figueroa", "Jefe de Postventa", "7"},
            {"Daniel", "Pizarro", "Soporte Técnico", "7"},
            {"Francisca", "Rivas", "Soporte Técnico", "7"},
            {"Ricardo", "Campos", "Soporte Técnico", "7"},
            {"Constanza", "Tapia", "Ejecutiva Postventa", "7"},
            {"Pablo", "Fuentes", "Ejecutivo Postventa", "7"},
            {"Macarena", "Vásquez", "Ejecutiva Postventa", "7"},
        };

        List<Empleado> lista = new ArrayList<>();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (String[] d : data) {
            Empleado e = new Empleado();
            e.setNombre(d[0]);
            e.setApellido(d[1]);
            e.setCargo(d[2]);
            e.setEmail((d[0] + "." + d[1] + "@grupocordillera.cl").toLowerCase());
            e.setDepartamento(deptos.get(Integer.parseInt(d[3])));
            e.setFechaContratacion(LocalDate.now().minusDays(rnd.nextLong(30, 1825)));
            e.setActivo(true);
            lista.add(e);
        }
        return lista;
    }
}
