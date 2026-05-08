package com.grupocordillera.datosorg.service.factory;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.repository.DepartamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpleadoFactoryTest {

    @Mock
    private DepartamentoRepository departamentoRepository;

    @InjectMocks
    private EmpleadoFactory empleadoFactory;

    @Test
    void testCrearEmpleado() {
        Departamento dept = new Departamento();
        dept.setId(1);
        dept.setNombre("TI");

        Empleado emp = empleadoFactory.crearEmpleado("Juan", "Pérez", "Desarrollador", dept);

        assertNotNull(emp);
        assertEquals("Juan", emp.getNombre());
        assertEquals("Pérez", emp.getApellido());
        assertEquals("Desarrollador", emp.getCargo());
        assertEquals(dept, emp.getDepartamento());
        assertTrue(emp.getActivo());
    }

    @Test
    void testCrearEmpleadosMasivos() {
        Departamento dept = new Departamento();
        dept.setId(1);
        dept.setNombre("General");

        when(departamentoRepository.findAll()).thenReturn(List.of(dept));

        List<Empleado> empleados = empleadoFactory.crearEmpleadosMasivos(10);

        assertNotNull(empleados);
        assertEquals(10, empleados.size());
        empleados.forEach(e -> {
            assertNotNull(e.getNombre());
            assertNotNull(e.getApellido());
            assertNotNull(e.getCargo());
            assertTrue(e.getActivo());
        });
    }

    @Test
    void testCrearEmpleadosMasivosSinDepartamentos() {
        when(departamentoRepository.findAll()).thenReturn(List.of());
        when(departamentoRepository.save(any())).thenAnswer(invocation -> {
            Departamento d = invocation.getArgument(0);
            d.setId(1);
            return d;
        });

        List<Empleado> empleados = empleadoFactory.crearEmpleadosMasivos(3);

        assertNotNull(empleados);
        assertEquals(3, empleados.size());
    }


}
