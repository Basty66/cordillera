package com.grupocordillera.ms_ventas.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class VentaRepositoryImpl implements VentaRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Map<String, Object>> generarReporteVentasPorSucursal() {
        List<Object[]> resultados = entityManager
                .createQuery("""
                    SELECT s.nombre, s.ciudad, COUNT(v), SUM(v.montoTotal), AVG(v.montoTotal)
                    FROM Venta v JOIN v.sucursal s
                    GROUP BY s.id, s.nombre, s.ciudad
                    ORDER BY SUM(v.montoTotal) DESC
                    """, Object[].class)
                .getResultList();

        return resultados.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("sucursal", r[0]);
            map.put("ciudad", r[1]);
            map.put("totalVentas", r[2]);
            map.put("montoTotal", r[3]);
            map.put("promedioVenta", r[4]);
            return map;
        }).toList();
    }

    @Override
    public Map<String, Object> obtenerResumenVentas() {
        Object resultado = entityManager
                .createQuery("SELECT COUNT(v), COALESCE(SUM(v.montoTotal), 0), COALESCE(AVG(v.montoTotal), 0) FROM Venta v", Object[].class)
                .getSingleResult();

        Object[] fila = (Object[]) resultado;
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalVentas", fila[0]);
        resumen.put("montoTotal", fila[1]);
        resumen.put("promedioVenta", fila[2]);
        return resumen;
    }

    @Override
    public BigDecimal calcularTotalVentasPorPeriodo(String inicio, String fin) {
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("ventas.calcular_ventas_periodo")
                    .registerStoredProcedureParameter("fecha_inicio", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("fecha_fin", String.class, ParameterMode.IN)
                    .registerStoredProcedureParameter("total", BigDecimal.class, ParameterMode.OUT)
                    .setParameter("fecha_inicio", inicio)
                    .setParameter("fecha_fin", fin);

            query.execute();
            return (BigDecimal) query.getOutputParameterValue("total");
        } catch (Exception e) {
            return null;
        }
    }
}
