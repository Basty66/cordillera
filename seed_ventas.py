import psycopg2
from psycopg2.extras import execute_values
from datetime import datetime, date
import random, time

conn = psycopg2.connect(host="ep-lucky-cloud-ami42tlw-pooler.c-5.us-east-1.aws.neon.tech", port=5432, dbname="neondb", user="neondb_owner", password="npg_RkUIf1wX4uDx", sslmode="require", connect_timeout=10)
conn.autocommit = True
cur = conn.cursor()
cur.execute("SET search_path TO ventas")

cur.execute("SELECT id FROM ventas.productos ORDER BY id")
product_ids = [r[0] for r in cur.fetchall()]
cur.execute("SELECT id FROM ventas.sucursales ORDER BY id")
suc_ids = [r[0] for r in cur.fetchall()]

rnd = random.Random(42)
precios = [1499900,459990,89990,45990,79990,35990,29990,109990,39990,65990,299990,189990,129990,599990,499990,45990,79990,89990,69990,29990,24990,45990,89990,79990,15990,29990,39990,69990,12990,34990,599990,89990,9990,14990,12990,799990,39990,24990,29990,19990,15990,8990,6990,4990,12990,3990,4990,3990,1890,3490,24990,29990,22990,19990,34990,15990,21990,18990,14990,27990]

ym = (2025, 6)
end = (2026, 5)
ventas_data = []
detalles_data = []

while ym <= end:
    y, m = ym
    days = (date(y, m + 1, 1) - date(y, m, 1)).days if m < 12 else (date(y + 1, 1, 1) - date(y, m, 1)).days
    nv = rnd.randint(65, 80)
    for _ in range(nv):
        d = rnd.randint(1, days)
        h = rnd.randint(9, 19)
        dt = datetime(y, m, d, h, rnd.randint(0, 59))
        sid = rnd.choice(suc_ids)
        uid = rnd.randint(1, 7)
        nd = rnd.randint(1, 5)
        total = 0.0
        dets = []
        for _ in range(nd):
            pi = rnd.choice(product_ids)
            ca = rnd.randint(1, 5)
            pr = float(rnd.choice(precios))
            total += pr * ca
            dets.append((pi, ca, pr))
        ventas_data.append((dt, sid, uid, round(total, 2)))
        detalles_data.append(dets)
    ym = (y + 1, 1) if m == 12 else (y, m + 1)

print(f"Ventas: {len(ventas_data)}, Detalles: {sum(len(d) for d in detalles_data)}", flush=True)

# Insert ventas in bulk
t0 = time.time()
ventas_for_db = [(v[0], v[1], v[2], v[3], v[3]) for v in ventas_data]
execute_values(cur, "INSERT INTO ventas.transacciones_venta (fecha_venta, sucursal_id, usuario_id, monto_total, precio_total) VALUES %s", ventas_for_db)
print(f"Ventas insertadas en {time.time()-t0:.1f}s", flush=True)

# Read IDs
cur.execute("SELECT id FROM ventas.transacciones_venta ORDER BY id")
ids = [r[0] for r in cur.fetchall()]
print(f"IDs: {ids[0]}..{ids[-1]} ({len(ids)})", flush=True)

# Insert detalles in bulk
t0 = time.time()
det_rows = [(ids[i], pi, ca, pr) for i, dets in enumerate(detalles_data) for pi, ca, pr in dets]
execute_values(cur, "INSERT INTO ventas.detalle_ventas (venta_id, producto_id, cantidad, precio_unitario) VALUES %s", det_rows)
print(f"Detalles insertados ({len(det_rows)}) en {time.time()-t0:.1f}s", flush=True)

# Reset sequences
cur.execute("SELECT setval('ventas.detalle_ventas_id_seq', COALESCE((SELECT MAX(id) FROM ventas.detalle_ventas), 1))")
print("¡LISTO!", flush=True)
cur.close(); conn.close()
