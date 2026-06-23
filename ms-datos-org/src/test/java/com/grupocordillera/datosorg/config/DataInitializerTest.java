package com.grupocordillera.datosorg.config;

import com.grupocordillera.datosorg.entity.Departamento;
import com.grupocordillera.datosorg.entity.Empleado;
import com.grupocordillera.datosorg.repository.DepartamentoRepository;
import com.grupocordillera.datosorg.repository.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private DepartamentoRepository departamentoRepository;
    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void testRun_whenDataExists_shouldSkip() {
        when(departamentoRepository.count()).thenReturn(1L);

        dataInitializer.run();

        verify(departamentoRepository, never()).saveAll(any());
        verify(empleadoRepository, never()).saveAll(any());
    }

    @Test
    void testRun_whenNoData_shouldSeedDepartamentosAndEmpleados() {
        when(departamentoRepository.count()).thenReturn(0L);
        List<Departamento> allDeptos = new ArrayList<>();
        when(departamentoRepository.findAll()).thenReturn(allDeptos);
        doAnswer(inv -> { allDeptos.addAll(inv.getArgument(0)); return inv.getArgument(0); })
            .when(departamentoRepository).saveAll(any());

        dataInitializer.run();

        verify(departamentoRepository).saveAll(any());
        verify(empleadoRepository).saveAll(any());
        assertEquals(8, allDeptos.size());
        assertEquals("Gerencia General", allDeptos.get(0).getNombre());
        assertEquals("Postventa", allDeptos.get(7).getNombre());
    }

    @Test
    void testCrearDepartamentos_createsEightDepartments() {
        when(departamentoRepository.count()).thenReturn(0L);
        final List<Departamento> allDeptos = new ArrayList<>();
        when(departamentoRepository.findAll()).thenReturn(allDeptos);
        doAnswer(inv -> { allDeptos.addAll(inv.getArgument(0)); return inv.getArgument(0); })
            .when(departamentoRepository).saveAll(any());

        dataInitializer.run();

        assertEquals(8, allDeptos.size());
        assertTrue(allDeptos.stream().anyMatch(d -> d.getNombre().equals("Ventas")));
        assertTrue(allDeptos.stream().anyMatch(d -> d.getNombre().equals("TI")));
        assertTrue(allDeptos.stream().anyMatch(d -> d.getNombre().equals("Recursos Humanos")));
    }

    @Test
    void testCrearEmpleados_creates35Empleados() {
        when(departamentoRepository.count()).thenReturn(0L);
        final List<Departamento> allDeptos = new ArrayList<>();
        when(departamentoRepository.findAll()).thenReturn(allDeptos);
        doAnswer(inv -> { allDeptos.addAll(inv.getArgument(0)); return inv.getArgument(0); })
            .when(departamentoRepository).saveAll(any());

        dataInitializer.run();

        verify(empleadoRepository).saveAll((List<Empleado>) argThat(list -> {
            List<Empleado> empleados = (List<Empleado>) list;
            assertEquals(35, empleados.size());
            return true;
        }));
    }
}
