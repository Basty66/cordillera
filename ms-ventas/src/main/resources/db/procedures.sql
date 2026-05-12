-- ============================================================
-- STORED PROCEDURES — Data Warehouse de Ventas
-- Esquema: ventas
-- ============================================================

-- 1. Calcular total de ventas en un período
CREATE OR REPLACE FUNCTION ventas.calcular_ventas_periodo(
    fecha_inicio TEXT,
    fecha_fin TEXT,
    OUT total NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT COALESCE(SUM(monto_total), 0) INTO total
    FROM ventas.transacciones_venta
    WHERE fecha_venta >= fecha_inicio::TIMESTAMP
      AND fecha_venta <= fecha_fin::TIMESTAMP;
END;
$$;

-- 2. Obtener top productos más vendidos
CREATE OR REPLACE FUNCTION ventas.top_productos(limite INT DEFAULT 10)
RETURNS TABLE(
    producto_id INT,
    nombre VARCHAR,
    total_vendido INT,
    monto_total NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.nombre, SUM(dv.cantidad)::INT, SUM(dv.subtotal)
    FROM ventas.detalle_ventas dv
    JOIN ventas.productos p ON p.id = dv.producto_id
    GROUP BY p.id, p.nombre
    ORDER BY SUM(dv.cantidad) DESC
    LIMIT limite;
END;
$$;

-- 3. Ventas agrupadas por mes (Data Warehouse)
CREATE OR REPLACE FUNCTION ventas.resumen_mensual()
RETURNS TABLE(
    anio INT,
    mes INT,
    nombre_mes TEXT,
    total_ventas BIGINT,
    monto_total NUMERIC,
    promedio NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        EXTRACT(YEAR FROM tv.fecha_venta)::INT,
        EXTRACT(MONTH FROM tv.fecha_venta)::INT,
        TO_CHAR(tv.fecha_venta, 'TMMonth'),
        COUNT(*)::BIGINT,
        SUM(tv.monto_total),
        AVG(tv.monto_total)
    FROM ventas.transacciones_venta tv
    GROUP BY EXTRACT(YEAR FROM tv.fecha_venta), EXTRACT(MONTH FROM tv.fecha_venta)
    ORDER BY 1, 2;
END;
$$;

-- 4. Ventas por categoría de producto (basado en primera palabra del nombre)
CREATE OR REPLACE FUNCTION ventas.resumen_categorias()
RETURNS TABLE(
    categoria TEXT,
    total_vendido BIGINT,
    monto_total NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        LEFT(p.nombre, POSITION(' ' IN p.nombre || ' ') - 1),
        SUM(dv.cantidad)::BIGINT,
        SUM(dv.subtotal)
    FROM ventas.detalle_ventas dv
    JOIN ventas.productos p ON p.id = dv.producto_id
    GROUP BY 1
    ORDER BY 3 DESC;
END;
$$;

-- 5. Totales generales del data warehouse
CREATE OR REPLACE FUNCTION ventas.totales_dashboard()
RETURNS TABLE(
    total_ventas BIGINT,
    monto_total NUMERIC,
    ticket_promedio NUMERIC,
    total_productos INT,
    total_sucursales INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        (SELECT COUNT(*) FROM ventas.transacciones_venta)::BIGINT,
        COALESCE((SELECT SUM(monto_total) FROM ventas.transacciones_venta), 0),
        COALESCE((SELECT AVG(monto_total) FROM ventas.transacciones_venta), 0),
        (SELECT COUNT(*) FROM ventas.productos)::INT,
        (SELECT COUNT(*) FROM ventas.sucursales)::INT;
END;
$$;
