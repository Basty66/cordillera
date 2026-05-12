package com.grupocordillera.indicadores.config;

import com.grupocordillera.indicadores.entity.CategoriaIndicador;
import com.grupocordillera.indicadores.entity.Indicador;
import com.grupocordillera.indicadores.entity.ValorIndicador;
import com.grupocordillera.indicadores.repository.CategoriaIndicadorRepository;
import com.grupocordillera.indicadores.repository.IndicadorRepository;
import com.grupocordillera.indicadores.repository.ValorIndicadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoriaIndicadorRepository categoriaRepository;
    private final IndicadorRepository indicadorRepository;
    private final ValorIndicadorRepository valorRepository;

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() > 0) {
            log.info("Indicadores ya existen — omitiendo carga histórica");
            return;
        }
        log.info("=== INICIANDO CARGA DE INDICADORES E HISTÓRICO ===");

        // Crear categorías
        CategoriaIndicador ventas = crearCategoria("Ventas", "Indicadores del área de ventas");
        CategoriaIndicador inventario = crearCategoria("Inventario", "Indicadores de gestión de inventario");
        CategoriaIndicador rentabilidad = crearCategoria("Rentabilidad", "Indicadores de rentabilidad financiera");

        // Crear indicadores
        Indicador ticketPromedio = crearIndicador("Ticket Promedio", "Ventas totales / N° transacciones", "CLP", "mensual", ventas);
        Indicador rotacionInventario = crearIndicador("Rotación de Inventario", "(Inventario actual / Inventario inicial) x 100", "%", "mensual", inventario);
        Indicador margenRentabilidad = crearIndicador("Margen de Rentabilidad", "((Ingresos - Costos) / Ingresos) x 100", "%", "mensual", rentabilidad);
        Indicador satisfaccion = crearIndicador("Satisfacción del Cliente", "Encuesta NPS post-venta", "pts", "mensual", ventas);
        Indicador crecimiento = crearIndicador("Crecimiento Interanual", "((Ventas mes actual / Ventas mes año anterior) - 1) x 100", "%", "mensual", rentabilidad);

        List<Indicador> indicadores = List.of(ticketPromedio, rotacionInventario, margenRentabilidad, satisfaccion, crecimiento);

        // Generar 12 meses de valores históricos (Jun 2025 - May 2026)
        List<ValorIndicador> historicos = new ArrayList<>();
        YearMonth start = YearMonth.of(2025, 6);
        YearMonth end = YearMonth.of(2026, 5);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        YearMonth ym = start;
        while (!ym.isAfter(end)) {
            String periodo = ym.toString();
            LocalDate fechaCalc = LocalDate.of(ym.getYear(), ym.getMonth(), rnd.nextInt(1, 26));

            for (Indicador ind : indicadores) {
                double valorBase = getValorBase(ind.getNombre());
                double variacion = rnd.nextDouble(-0.15, 0.15);
                BigDecimal valor = BigDecimal.valueOf(valorBase * (1 + variacion))
                        .setScale(2, java.math.RoundingMode.HALF_UP);

                ValorIndicador vi = new ValorIndicador();
                vi.setIndicador(ind);
                vi.setValor(valor);
                vi.setPeriodo(periodo);
                vi.setFechaCalculo(fechaCalc);
                historicos.add(vi);
            }
            ym = ym.plusMonths(1);
        }

        valorRepository.saveAll(historicos);
        log.info("{} valores históricos de indicadores creados (12 meses x 5 KPIs)", historicos.size());
    }

    private CategoriaIndicador crearCategoria(String nombre, String descripcion) {
        CategoriaIndicador c = new CategoriaIndicador();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        return categoriaRepository.save(c);
    }

    private Indicador crearIndicador(String nombre, String formula, String unidad, String frecuencia, CategoriaIndicador cat) {
        Indicador i = new Indicador();
        i.setNombre(nombre);
        i.setFormula(formula);
        i.setUnidad(unidad);
        i.setFrecuencia(frecuencia);
        i.setCategoria(cat);
        return indicadorRepository.save(i);
    }

    private double getValorBase(String nombre) {
        return switch (nombre) {
            case "Ticket Promedio" -> 45000;
            case "Rotación de Inventario" -> 75;
            case "Margen de Rentabilidad" -> 32;
            case "Satisfacción del Cliente" -> 8.2;
            case "Crecimiento Interanual" -> 12;
            default -> 50;
        };
    }
}
