package com.grupocordillera.ms_ventas.repository;

import com.grupocordillera.ms_ventas.dto.TopProductoDTO;
import com.grupocordillera.ms_ventas.dto.VentaCategoriaDTO;
import com.grupocordillera.ms_ventas.dto.VentaMensualDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Object[]> typedQuery;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private VentaRepositoryImpl ventaRepository;

    @Test
    void testGenerarReporteVentasPorSucursal() {
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(
                new Object[]{"Suc A", "Santiago", 10L, BigDecimal.valueOf(500000), BigDecimal.valueOf(50000)}
        ));

        List<Map<String, Object>> result = ventaRepository.generarReporteVentasPorSucursal();
        assertEquals(1, result.size());
        assertEquals("Suc A", result.get(0).get("sucursal"));
        assertEquals("Santiago", result.get(0).get("ciudad"));
    }

    @Test
    void testObtenerResumenVentas() {
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(
                new Object[]{10L, BigDecimal.valueOf(500000), 50000.0}
        ));

        Map<String, Object> result = ventaRepository.obtenerResumenVentas();
        assertEquals(10L, result.get("totalVentas"));
        assertEquals(BigDecimal.valueOf(500000), result.get("montoTotal"));
    }

    @Test
    void testObtenerResumenVentas_EmptyResult() {
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        Map<String, Object> result = ventaRepository.obtenerResumenVentas();
        assertEquals(0L, result.get("totalVentas"));
        assertEquals(BigDecimal.ZERO, result.get("montoTotal"));
    }

    @Test
    void testCalcularTotalVentasPorPeriodo() {
        when(entityManager.createStoredProcedureQuery("ventas.calcular_ventas_periodo")).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(BigDecimal.class), eq(ParameterMode.OUT))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("total")).thenReturn(BigDecimal.valueOf(100000));

        BigDecimal result = ventaRepository.calcularTotalVentasPorPeriodo("2026-01-01", "2026-01-31");
        assertEquals(BigDecimal.valueOf(100000), result);
    }

    @Test
    void testCalcularTotalVentasPorPeriodo_Exception() {
        when(entityManager.createStoredProcedureQuery("ventas.calcular_ventas_periodo")).thenThrow(new RuntimeException("SP not found"));

        BigDecimal result = ventaRepository.calcularTotalVentasPorPeriodo("2026-01-01", "2026-01-31");
        assertNull(result);
    }

    @Test
    void testVentasMensuales() {
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(
                new Object[]{"2026-01", 10L, BigDecimal.valueOf(500000), 50000.0},
                new Object[]{"2026-02", 15L, BigDecimal.valueOf(750000), 50000.0}
        ));

        List<VentaMensualDTO> result = ventaRepository.ventasMensuales();
        assertEquals(2, result.size());
        assertEquals("Enero", result.get(0).mes());
        assertEquals("Febrero", result.get(1).mes());
    }

    @Test
    void testVentasPorCategoria() {
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(
                new Object[]{"Electro", 5L, BigDecimal.valueOf(300000)}
        ));

        List<VentaCategoriaDTO> result = ventaRepository.ventasPorCategoria();
        assertEquals(1, result.size());
        assertEquals("Electro", result.get(0).categoria());
    }

    @Test
    void testTopProductos() {
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(5)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(
                new Object[]{1, "Producto A", 20L, BigDecimal.valueOf(400000)}
        ));

        List<TopProductoDTO> result = ventaRepository.topProductos(5);
        assertEquals(1, result.size());
        assertEquals("Producto A", result.get(0).nombre());
        assertEquals(1, result.get(0).productoId());
    }
}
