CREATE SCHEMA IF NOT EXISTS ventas;

-- Covering indexes for report queries
CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas.transacciones_venta (fecha_venta);
CREATE INDEX IF NOT EXISTS idx_ventas_sucursal ON ventas.transacciones_venta (sucursal_id);
CREATE INDEX IF NOT EXISTS idx_ventas_fecha_monto ON ventas.transacciones_venta (fecha_venta, monto_total);
CREATE INDEX IF NOT EXISTS idx_detalle_ventas_producto ON ventas.detalle_ventas (producto_id);
CREATE INDEX IF NOT EXISTS idx_detalle_ventas_venta ON ventas.detalle_ventas (venta_id);
