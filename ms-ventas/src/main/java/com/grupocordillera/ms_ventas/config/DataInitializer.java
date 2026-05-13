package com.grupocordillera.ms_ventas.config;

import com.grupocordillera.ms_ventas.entity.DetalleVenta;
import com.grupocordillera.ms_ventas.entity.Producto;
import com.grupocordillera.ms_ventas.entity.Sucursal;
import com.grupocordillera.ms_ventas.entity.Venta;
import com.grupocordillera.ms_ventas.repository.DetalleVentaRepository;
import com.grupocordillera.ms_ventas.repository.ProductoRepository;
import com.grupocordillera.ms_ventas.repository.SucursalRepository;
import com.grupocordillera.ms_ventas.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final SucursalRepository sucursalRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    @Override
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            log.info("Datos ya existen — omitiendo inicialización");
            return;
        }
        log.info("=== INICIANDO CARGA MASIVA DE DATOS ===");
        long start = System.currentTimeMillis();
        seedData();
        long elapsed = (System.currentTimeMillis() - start) / 1000;
        log.info("=== CARGA COMPLETADA en {}s ===", elapsed);
    }

    @Transactional
    protected void seedData() {
        detalleVentaRepository.deleteAllInBatch();
        ventaRepository.deleteAllInBatch();
        productoRepository.deleteAllInBatch();
        sucursalRepository.deleteAllInBatch();

        List<Producto> productos = crearProductos();
        productoRepository.saveAll(productos);
        productos = productoRepository.findAll();
        log.info("{} productos creados", productos.size());

        List<Sucursal> sucursales = crearSucursales();
        sucursalRepository.saveAll(sucursales);
        sucursales = sucursalRepository.findAll();
        log.info("{} sucursales creadas", sucursales.size());

        long ventasCreadas = crearVentasHistoricas(productos, sucursales);
        log.info("{} ventas históricas creadas", ventasCreadas);
    }

    private List<Producto> crearProductos() {
        List<Producto> lista = new ArrayList<>();
        int id = 0;

        for (Categoria cat : Categoria.values()) {
            BigDecimal[] precios = cat.getPrecios();
            int[] stocks = cat.getStocks();
            for (int i = 0; i < 10; i++) {
                id++;
                Producto p = new Producto();
                String nombre = cat.nombres[i];
                p.setNombre(nombre);
                p.setDescripcion(cat.descripciones[i]);
                p.setPrecio(precios[i]);
                p.setStock(stocks[i]);
                String catKey = cat.name().toLowerCase();
                p.setImagenUrl("https://picsum.photos/seed/" + catKey + id + "/400/300");
                lista.add(p);
            }
        }
        return lista;
    }

    private List<Sucursal> crearSucursales() {
        String[][] data = {
            {"Sucursal Santiago Centro", "Santiago", "Av. Libertador Bernardo O'Higgins 1234"},
            {"Sucursal Providencia", "Santiago", "Av. Providencia 2560"},
            {"Sucursal Las Condes", "Santiago", "Av. Apoquindo 4500"},
            {"Sucursal Valparaíso", "Valparaíso", "Calle Prat 567"},
            {"Sucursal Viña del Mar", "Viña del Mar", "Av. San Martín 890"},
            {"Sucursal Concepción", "Concepción", "Av. Libertad 2345"},
            {"Sucursal La Serena", "La Serena", "Av. Las Higueras 123"},
            {"Sucursal Antofagasta", "Antofagasta", "Av. José Miguel Carrera 789"},
            {"Sucursal Temuco", "Temuco", "Av. Alemania 456"},
            {"Sucursal Rancagua", "Rancagua", "Calle Estado 789"},
            {"Sucursal Iquique", "Iquique", "Av. Arturo Prat 345"},
            {"Sucursal Puerto Montt", "Puerto Montt", "Av. Diego Portales 678"},
        };
        List<Sucursal> lista = new ArrayList<>();
        int i = 0;
        for (String[] d : data) {
            i++;
            Sucursal s = new Sucursal();
            s.setNombre(d[0]);
            s.setCiudad(d[1]);
            s.setDireccion(d[2]);
            String cityKey = d[1].toLowerCase().replace(" ", "").replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u");
            s.setImagenUrl("https://picsum.photos/seed/sucursal" + cityKey + i + "/600/400");
            lista.add(s);
        }
        return lista;
    }

    protected long crearVentasHistoricas(List<Producto> productos, List<Sucursal> sucursales) {
        long count = 0;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        YearMonth start = YearMonth.of(2025, 6);
        YearMonth end = YearMonth.of(2026, 5);
        List<Venta> batch = new ArrayList<>();

        YearMonth ym = start;
        while (!ym.isAfter(end)) {
            int daysInMonth = ym.lengthOfMonth();
            int ventasEsteMes = rnd.nextInt(60, 80);
            for (int v = 0; v < ventasEsteMes; v++) {
                int day = rnd.nextInt(1, daysInMonth + 1);
                int hour = rnd.nextInt(9, 20);
                int minute = rnd.nextInt(0, 60);
                LocalDateTime fecha = LocalDateTime.of(LocalDate.of(ym.getYear(), ym.getMonth(), day), LocalTime.of(hour, minute));

                Sucursal suc = sucursales.get(rnd.nextInt(sucursales.size()));
                int numDetalles = rnd.nextInt(1, 6);
                BigDecimal total = BigDecimal.ZERO;
                Venta venta = new Venta();
                venta.setFechaVenta(fecha);
                venta.setSucursal(suc);
                venta.setUsuarioId(rnd.nextInt(1, 8));

                for (int d = 0; d < numDetalles; d++) {
                    Producto prod = productos.get(rnd.nextInt(productos.size()));
                    int cant = rnd.nextInt(1, 6);
                    BigDecimal subtotal = prod.getPrecio().multiply(BigDecimal.valueOf(cant));
                    total = total.add(subtotal);

                    DetalleVenta det = new DetalleVenta();
                    det.setProducto(prod);
                    det.setCantidad(cant);
                    det.setPrecioUnitario(prod.getPrecio());
                    venta.agregarDetalle(det);
                }
                venta.setPrecioTotal(total);
                venta.setMontoTotal(total);
                batch.add(venta);
                count++;

                if (batch.size() >= 100) {
                    ventaRepository.saveAll(batch);
                    batch.clear();
                }
            }
            ym = ym.plusMonths(1);
        }
        if (!batch.isEmpty()) {
            ventaRepository.saveAll(batch);
        }
        return count;
    }

    private enum Categoria {
        ELECTRONICA(
            new String[]{"Laptop Gamer X1", "Monitor 27\" 4K", "Teclado Mecánico RGB", "Mouse Inalámbrico", "Audífonos Bluetooth", "Webcam HD 1080p", "Hub USB-C 7 puertos", "Disco SSD 1TB", "Parlante Portátil", "Micrófono Profesional"},
            new String[]{"Laptop de alto rendimiento con GPU dedicada", "Monitor IPS 27 pulgadas resolución 4K", "Teclado mecánico retroiluminado RGB", "Mouse ergonómico inalámbrico con 6 botones", "Audífonos con cancelación de ruido activa", "Cámara web full HD con micrófono integrado", "Hub multipuerto USB-C con HDMI y SD", "Disco de estado sólido NVMe 1TB", "Parlante Bluetooth resistente al agua", "Micrófono cardioide para streaming y podcast"},
            "1499900,459990,89990,45990,79990,35990,29990,109990,39990,65990",
            "50,80,120,200,100,150,90,60,110,40"
        ),
        HOGAR(
            new String[]{"Aspiradora Robot", "Cafetera Automática", "Horno Eléctrico", "Refrigerador No Frost", "Lavadora Digital", "Plancha a Vapor", "Ventilador de Torre", "Microondas Digital", "Licuadora Profesional", "Tostadora 2 Ranuras"},
            new String[]{"Aspiradora robot con mapeo inteligente", "Cafetera programable con molinillo integrado", "Horno eléctrico 45L con convección", "Refrigerador No Frost 400L con dispensador", "Lavadora frontal 15kg con conexión WiFi", "Plancha a vapor 2400W con suela de cerámica", "Ventilador torre silencioso con control remoto", "Microondas digital 30L con grill", "Licuadora 1200W con jarra de vidrio", "Tostadora con 6 niveles de dorado y bandeja"},
            "299990,189990,129990,599990,499990,45990,79990,89990,69990,29990",
            "20,25,15,10,12,30,25,20,18,35"
        ),
        ROPA(
            new String[]{"Polera Algodón Premium", "Pantalón Chino", "Chaqueta Impermeable", "Zapatos Casual", "Gorro de Lana", "Bufanda de Seda", "Camisa Oxford", "Vestido Elegante", "Calcetines Térmicos", "Guantes de Cuero"},
            new String[]{"Polera de algodón peinado 180 hilos", "Pantalón chino slim fit stretch", "Chaqueta impermeable con capucha desmontable", "Zapatos casual de cuero genuino", "Gorro de lana merino tejido a mano", "Bufanda de seda natural estampada", "Camisa Oxford de manga larga", "Vestido elegante corte midi", "Calcetines térmicos con compresión", "Guantes de cuero con forro polar"},
            "24990,45990,89990,79990,15990,29990,39990,69990,12990,34990",
            "50,40,25,30,35,20,45,15,60,20"
        ),
        DEPORTES(
            new String[]{"Bicicleta MTB 29\"", "Set de Pesas 20kg", "Cuerda para Saltar", "Botella Deportiva", "Toalla Microfibra", "Cinta de Correr", "Balón de Fútbol", "Rodillo de Espuma", "Colchoneta Yoga", "Termo Deportivo"},
            new String[]{"Bicicleta montañesa 29 velocidades", "Set de pesas con mancuernas y barra", "Cuerda para saltar ajustable con rodamientos", "Botella deportiva 750ml libre de BPA", "Toalla de microfibra de secado rápido", "Cinta de correr plegable con pantalla LCD", "Balón de fútbol profesional cosido a mano", "Rodillo de espuma para recuperación muscular", "Colchoneta de yoga antideslizante 6mm", "Termo deportivo 1L con aislamiento al vacío"},
            "599990,89990,9990,14990,12990,799990,39990,24990,29990,19990",
            "10,20,40,50,30,8,25,15,20,35"
        ),
        ALIMENTOS(
            new String[]{"Café Grano 1kg", "Té Verde Orgánico", "Arroz Grado 1 5kg", "Fideos Artesanales", "Aceite de Oliva Extra", "Sal Marina 1kg", "Azúcar Morena 2kg", "Harina de Trigo 3kg", "Leche Entera 1L", "Galletas de Avena"},
            new String[]{"Café arábica 100% tostado medio", "Té verde orgánico en hebras 100 bolsitas", "Arroz de grano largo grado 1", "Fideos artesanales de sémola de trigo", "Aceite de oliva virgen extra 500ml", "Sal marina natural sin refinar", "Azúcar morena orgánica sin blanquear", "Harina de trigo fortificada 0000", "Leche entera pasteurizada 1 litro", "Galletas de avena con chocolate"},
            "15990,8990,6990,4990,12990,3990,4990,3990,1890,3490",
            "50,40,80,30,45,60,55,70,90,65"
        ),
        LIBROS(
            new String[]{"100 Años de Soledad", "Don Quijote de la Mancha", "La Casa de los Espíritus", "1984 de George Orwell", "Sapiens: De Animales a Dioses", "El Principito", "Ficciones de Borges", "Cien Poemas de Amor", "El Arte de la Guerra", "Harry Potter y la Piedra Filosofal"},
            new String[]{"Novela icónica de Gabriel García Márquez", "Clásico universal de Miguel de Cervantes", "Obra maestra de Isabel Allende", "Distopía fundamental del siglo XX", "Breve historia de la humanidad de Yuval Harari", "Fábula poética de Antoine de Saint-Exupéry", "Colección de cuentos de Jorge Luis Borges", "Antología poética de Pablo Neruda", "Tratado militar de Sun Tzu", "Primera entrega de J.K. Rowlings"},
            "24990,29990,22990,19990,34990,15990,21990,18990,14990,27990",
            "30,25,20,35,28,40,22,18,32,38"
        );

        final String[] nombres;
        final String[] descripciones;
        final String preciosCsv;
        final String stocksCsv;

        Categoria(String[] nombres, String[] descripciones, String preciosCsv, String stocksCsv) {
            this.nombres = nombres;
            this.descripciones = descripciones;
            this.preciosCsv = preciosCsv;
            this.stocksCsv = stocksCsv;
        }

        BigDecimal[] getPrecios() {
            String[] parts = preciosCsv.split(",");
            BigDecimal[] result = new BigDecimal[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = BigDecimal.valueOf(Long.parseLong(parts[i]));
            }
            return result;
        }

        int[] getStocks() {
            String[] parts = stocksCsv.split(",");
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Integer.parseInt(parts[i]);
            }
            return result;
        }
    }
}
