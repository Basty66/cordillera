import psycopg2
from datetime import datetime, date
from decimal import Decimal
import random, sys

conn = psycopg2.connect(
    host="ep-lucky-cloud-ami42tlw-pooler.c-5.us-east-1.aws.neon.tech",
    port=5432, dbname="neondb",
    user="neondb_owner", password="npg_RkUIf1wX4uDx",
    sslmode="require", connect_timeout=10
)
conn.autocommit = False
cur = conn.cursor()

cur.execute("CREATE SCHEMA IF NOT EXISTS ventas; SET search_path TO ventas")
cur.execute("TRUNCATE TABLE detalle_ventas, transacciones_venta, productos, sucursales RESTART IDENTITY CASCADE")
print("Tablas truncadas", flush=True)

# Products
cats = [("Electrónica", [
    ("Laptop Gamer X1","Laptop de alto rendimiento con GPU dedicada",1499900,50),
    ('Monitor 27" 4K','Monitor IPS 27" 4K',459990,80),
    ("Teclado Mecánico RGB","Teclado mecánico retroiluminado RGB",89990,120),
    ("Mouse Inalámbrico","Mouse ergonómico inalámbrico",45990,200),
    ("Audífonos Bluetooth","Audífonos con cancelación de ruido activa",79990,100),
    ("Webcam HD 1080p","Cámara web full HD",35990,150),
    ("Hub USB-C 7 puertos","Hub multipuerto USB-C",29990,90),
    ("Disco SSD 1TB","SSD NVMe 1TB",109990,60),
    ("Parlante Portátil","Parlante Bluetooth resistente al agua",39990,110),
    ("Micrófono Profesional","Micrófono cardioide para streaming",65990,40)
]), ("Hogar", [
    ("Aspiradora Robot","Aspiradora robot con mapeo inteligente",299990,20),
    ("Cafetera Automática","Cafetera programable con molinillo",189990,25),
    ("Horno Eléctrico","Horno eléctrico 45L con convección",129990,15),
    ("Refrigerador No Frost","Refrigerador No Frost 400L",599990,10),
    ("Lavadora Digital","Lavadora frontal 15kg WiFi",499990,12),
    ("Plancha a Vapor","Plancha a vapor 2400W",45990,30),
    ("Ventilador de Torre","Ventilador torre silencioso",79990,25),
    ("Microondas Digital","Microondas digital 30L con grill",89990,20),
    ("Licuadora Profesional","Licuadora 1200W con jarra de vidrio",69990,18),
    ("Tostadora 2 Ranuras","Tostadora con 6 niveles de dorado",29990,35)
]), ("Ropa", [
    ("Polera Algodón Premium","Polera de algodón peinado 180 hilos",24990,50),
    ("Pantalón Chino","Pantalón chino slim fit stretch",45990,40),
    ("Chaqueta Impermeable","Chaqueta impermeable con capucha",89990,25),
    ("Zapatos Casual","Zapatos casual de cuero genuino",79990,30),
    ("Gorro de Lana","Gorro de lana merino tejido a mano",15990,35),
    ("Bufanda de Seda","Bufanda de seda natural estampada",29990,20),
    ("Camisa Oxford","Camisa Oxford manga larga",39990,45),
    ("Vestido Elegante","Vestido elegante corte midi",69990,15),
    ("Calcetines Térmicos","Calcetines térmicos con compresión",12990,60),
    ("Guantes de Cuero","Guantes de cuero con forro polar",34990,20)
]), ("Deportes", [
    ('Bicicleta MTB 29"',"Bicicleta montañesa 29 velocidades",599990,10),
    ("Set de Pesas 20kg","Set de pesas con mancuernas y barra",89990,20),
    ("Cuerda para Saltar","Cuerda para saltar ajustable",9990,40),
    ("Botella Deportiva","Botella deportiva 750ml",14990,50),
    ("Toalla Microfibra","Toalla de microfibra secado rápido",12990,30),
    ("Cinta de Correr","Cinta de correr plegable LCD",799990,8),
    ("Balón de Fútbol","Balón de fútbol profesional",39990,25),
    ("Rodillo de Espuma","Rodillo de espuma recuperación muscular",24990,15),
    ("Colchoneta Yoga","Colchoneta yoga antideslizante 6mm",29990,20),
    ("Termo Deportivo","Termo deportivo 1L aislamiento al vacío",19990,35)
]), ("Alimentos", [
    ("Café Grano 1kg","Café arábica 100% tostado medio",15990,50),
    ("Té Verde Orgánico","Té verde orgánico en hebras 100 bolsitas",8990,40),
    ("Arroz Grado 1 5kg","Arroz de grano largo grado 1",6990,80),
    ("Fideos Artesanales","Fideos artesanales de sémola",4990,30),
    ("Aceite de Oliva Extra","Aceite de oliva virgen extra 500ml",12990,45),
    ("Sal Marina 1kg","Sal marina natural sin refinar",3990,60),
    ("Azúcar Morena 2kg","Azúcar morena orgánica",4990,55),
    ("Harina de Trigo 3kg","Harina de trigo fortificada 0000",3990,70),
    ("Leche Entera 1L","Leche entera pasteurizada 1L",1890,90),
    ("Galletas de Avena","Galletas de avena con chocolate",3490,65)
]), ("Libros", [
    ("100 Años de Soledad","Novela de Gabriel García Márquez",24990,30),
    ("Don Quijote de la Mancha","Clásico de Miguel de Cervantes",29990,25),
    ("La Casa de los Espíritus","Obra maestra de Isabel Allende",22990,20),
    ("1984 de George Orwell","Distopía de George Orwell",19990,35),
    ("Sapiens: De Animales a Dioses","Historia de la humanidad de Harari",34990,28),
    ("El Principito","Fábula de Saint-Exupéry",15990,40),
    ("Ficciones de Borges","Cuentos de Jorge Luis Borges",21990,22),
    ("Cien Poemas de Amor","Poesía de Pablo Neruda",18990,18),
    ("El Arte de la Guerra","Tratado militar de Sun Tzu",14990,32),
    ("Harry Potter y la Piedra Filosofal","Primera entrega de J.K. Rowling",27990,38)
])]

prod_rows = []
cat_keys = {"Electrónica":"electronica","Hogar":"hogar","Ropa":"ropa","Deportes":"deportes","Alimentos":"alimentos","Libros":"libros"}
pid = 0
for cat, items in cats:
    for name, desc, price, stock in items:
        pid += 1
        img = f"https://picsum.photos/seed/{cat_keys[cat]}{pid}/400/300"
        prod_rows.append((name, desc, price, stock, img))

cur.executemany(
    "INSERT INTO ventas.productos (nombre, descripcion, precio, stock, imagen_url) VALUES (%s,%s,%s,%s,%s)",
    prod_rows
)
print("60 productos insertados", flush=True)

# Sucursales  
suc_rows = [
    ("Sucursal Santiago Centro","Santiago","Av. Libertador Bernardo O'Higgins 1234","https://picsum.photos/seed/sucursalsantiago1/600/400"),
    ("Sucursal Providencia","Santiago","Av. Providencia 2560","https://picsum.photos/seed/sucursalsantiago2/600/400"),
    ("Sucursal Las Condes","Santiago","Av. Apoquindo 4500","https://picsum.photos/seed/sucursalsantiago3/600/400"),
    ("Sucursal Valparaíso","Valparaíso","Calle Prat 567","https://picsum.photos/seed/sucursalvalparaiso4/600/400"),
    ("Sucursal Viña del Mar","Viña del Mar","Av. San Martín 890","https://picsum.photos/seed/sucursalvinadelmar5/600/400"),
    ("Sucursal Concepción","Concepción","Av. Libertad 2345","https://picsum.photos/seed/sucursalconcepcion6/600/400"),
    ("Sucursal La Serena","La Serena","Av. Las Higueras 123","https://picsum.photos/seed/sucursallaserena7/600/400"),
    ("Sucursal Antofagasta","Antofagasta","Av. José Miguel Carrera 789","https://picsum.photos/seed/sucursalantofagasta8/600/400"),
    ("Sucursal Temuco","Temuco","Av. Alemania 456","https://picsum.photos/seed/sucursaltemuco9/600/400"),
    ("Sucursal Rancagua","Rancagua","Calle Estado 789","https://picsum.photos/seed/sucursalrancagua10/600/400"),
    ("Sucursal Iquique","Iquique","Av. Arturo Prat 345","https://picsum.photos/seed/sucursaliquique11/600/400"),
    ("Sucursal Puerto Montt","Puerto Montt","Av. Diego Portales 678","https://picsum.photos/seed/sucursalpuertomontt12/600/400"),
]
cur.executemany(
    "INSERT INTO ventas.sucursales (nombre, ciudad, direccion, imagen_url) VALUES (%s,%s,%s,%s)",
    suc_rows
)
print("12 sucursales insertadas", flush=True)

# Read back IDs
cur.execute("SELECT id FROM ventas.productos ORDER BY id")
product_ids = [r[0] for r in cur.fetchall()]
cur.execute("SELECT id FROM ventas.sucursales ORDER BY id")
suc_ids = [r[0] for r in cur.fetchall()]

# Ventas
rnd = random.Random(42)
total_v = 0
ym_start, ym_end = (2025, 6), (2026, 5)
y, m = ym_start

while (y, m) <= ym_end:
    days = (date(y, m + 1, 1) - date(y, m, 1)).days if m < 12 else (date(y + 1, 1, 1) - date(y, m, 1)).days
    nv = rnd.randint(65, 80)
    for _ in range(nv):
        d = rnd.randint(1, days)
        h = rnd.randint(9, 19)
        mi = rnd.randint(0, 59)
        dt = datetime(y, m, d, h, mi)
        sid = rnd.choice(suc_ids)
        uid = rnd.randint(1, 7)
        nd = rnd.randint(1, 5)
        total = Decimal('0.00')

        cur.execute(
            "INSERT INTO ventas.transacciones_venta (fecha_venta, sucursal_id, usuario_id, monto_total, precio_total) VALUES (%s,%s,%s,0,0) RETURNING id",
            (dt, sid, uid)
        )
        vid = cur.fetchone()[0]
        det_rows = []
        for _ in range(nd):
            pi = rnd.choice(product_ids)
            ca = rnd.randint(1, 5)
            cur.execute("SELECT precio FROM ventas.productos WHERE id=%s", (pi,))
            pr = cur.fetchone()[0]
            st = Decimal(str(pr)) * Decimal(str(ca))
            total += st
            det_rows.append((vid, pi, ca, float(pr)))
        cur.executemany(
            "INSERT INTO ventas.detalle_ventas (venta_id, producto_id, cantidad, precio_unitario) VALUES (%s,%s,%s,%s)",
            det_rows
        )
        cur.execute("UPDATE ventas.transacciones_venta SET monto_total=%s, precio_total=%s WHERE id=%s",
                    (float(total), float(total), vid))
        total_v += 1
    if m == 12: y += 1; m = 1
    else: m += 1
    if total_v % 200 == 0:
        print(f"  {total_v} ventas...", flush=True)

conn.commit()
print(f"{total_v} ventas históricas insertadas", flush=True)

# Set sequences
cur.execute("SELECT setval('ventas.productos_id_seq', COALESCE((SELECT MAX(id) FROM ventas.productos), 1))")
cur.execute("SELECT setval('ventas.sucursales_id_seq', COALESCE((SELECT MAX(id) FROM ventas.sucursales), 1))")
cur.execute("SELECT setval('ventas.transacciones_venta_id_seq', COALESCE((SELECT MAX(id) FROM ventas.transacciones_venta), 1))")
cur.execute("SELECT setval('ventas.detalle_ventas_id_seq', COALESCE((SELECT MAX(id) FROM ventas.detalle_ventas), 1))")
conn.commit()
print("Secuencias actualizadas", flush=True)
print("¡DATOS CARGADOS EXITOSAMENTE!", flush=True)
cur.close()
conn.close()
