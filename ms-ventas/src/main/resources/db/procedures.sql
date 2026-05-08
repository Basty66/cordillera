-- Stored Procedure: Calcular total de ventas en un período
-- Esquema: ventas
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

-- Stored Procedure: Obtener top productos más vendidos
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
