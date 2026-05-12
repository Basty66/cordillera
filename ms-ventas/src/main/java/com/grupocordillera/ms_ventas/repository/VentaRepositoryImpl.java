package com.grupocordillera.ms_ventas.repository;

import com.grupocordillera.ms_ventas.dto.TopProductoDTO;
import com.grupocordillera.ms_ventas.dto.VentaCategoriaDTO;
import com.grupocordillera.ms_ventas.dto.VentaMensualDTO;
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

    @Override
    public List<VentaMensualDTO> ventasMensuales() {
        List<Object[]> resultados = entityManager
                .createQuery("""
                    SELECT FUNCTION('TO_CHAR', v.fechaVenta, 'YYYY-MM'),
                           FUNCTION('EXTRACT', 'YEAR', v.fechaVenta),
                           COUNT(v), SUM(v.montoTotal), AVG(v.montoTotal)
                    FROM Venta v
                    GROUP BY FUNCTION('TO_CHAR', v.fechaVenta, 'YYYY-MM'),
                             FUNCTION('EXTRACT', 'YEAR', v.fechaVenta)
                    ORDER BY FUNCTION('TO_CHAR', v.fechaVenta, 'YYYY-MM')
                    """, Object[].class)
                .getResultList();

        return resultados.stream().map(r -> {
            String mesAnio = (String) r[0];
            String[] partes = mesAnio.split("-");
            return new VentaMensualDTO(
                getNombreMes(Integer.parseInt(partes[1])),
                Integer.parseInt(partes[0]),
                (Long) r[2],
                (BigDecimal) r[3],
                (BigDecimal) r[4]
            );
        }).toList();
    }

    @Override
    public List<VentaCategoriaDTO> ventasPorCategoria() {
        List<Object[]> resultados = entityManager
                .createQuery("""
                    SELECT SUBSTRING(p.nombre, 1, CASE WHEN POSITION(' ' IN p.nombre) > 0
                        THEN POSITION(' ' IN p.nombre) - 1 ELSE LENGTH(p.nombre) END),
                           SUM(dv.cantidad), SUM(dv.cantidad * dv.precioUnitario)
                    FROM DetalleVenta dv JOIN dv.producto p
                    GROUP BY 1
                    ORDER BY 3 DESC
                    """, Object[].class)
                .getResultList();

        return resultados.stream().map(r -> new VentaCategoriaDTO(
            (String) r[0], (Long) r[1], (BigDecimal) r[2]
        )).toList();
    }

    @Override
    public List<TopProductoDTO> topProductos(int limite) {
        List<Object[]> resultados = entityManager
                .createQuery("""
                    SELECT p.id, p.nombre, SUM(dv.cantidad), SUM(dv.cantidad * dv.precioUnitario)
                    FROM DetalleVenta dv JOIN dv.producto p
                    GROUP BY p.id, p.nombre
                    ORDER BY SUM(dv.cantidad) DESC
                    """, Object[].class)
                .setMaxResults(limite)
                .getResultList();

        return resultados.stream().map(r -> new TopProductoDTO(
            (Integer) r[0], (String) r[1], (Long) r[2], (BigDecimal) r[3]
        )).toList();
    }

    private String getNombreMes(int mes) {
        String[] nombres = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return mes >= 1 && mes <= 12 ? nombres[mes] : "Mes " + mes;
    }
}
