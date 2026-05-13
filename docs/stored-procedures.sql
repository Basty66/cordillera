-- Procedimiento almacenado: calcular_ventas_periodo
-- Calcula el total de ventas en un rango de fechas
-- Schema: ventas

CREATE OR REPLACE FUNCTION ventas.calcular_ventas_periodo(
    fecha_inicio TEXT,
    fecha_fin TEXT
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    total NUMERIC;
BEGIN
    SELECT COALESCE(SUM(v.monto_total), 0)
    INTO total
    FROM ventas.transacciones_venta v
    WHERE v.fecha_venta >= fecha_inicio::DATE
      AND v.fecha_venta <= fecha_fin::DATE;

    RETURN total;
END;
$$;
