package com.grupocordillera.indicadores.service;

import com.grupocordillera.indicadores.entity.CategoriaIndicador;
import com.grupocordillera.indicadores.entity.Indicador;
import com.grupocordillera.indicadores.entity.ValorIndicador;
import com.grupocordillera.indicadores.repository.CategoriaIndicadorRepository;
import com.grupocordillera.indicadores.repository.IndicadorRepository;
import com.grupocordillera.indicadores.repository.ValorIndicadorRepository;
import com.grupocordillera.indicadores.service.factory.CalculoIndicadorFactory;
import com.grupocordillera.indicadores.service.factory.CalculoStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndicadorService {

    private final IndicadorRepository indicadorRepository;
    private final ValorIndicadorRepository valorIndicadorRepository;
    private final CategoriaIndicadorRepository categoriaIndicadorRepository;
    private final CalculoIndicadorFactory calculoFactory;

    @Cacheable("categorias")
    public List<CategoriaIndicador> obtenerCategorias() {
        return categoriaIndicadorRepository.findAll();
    }

    @CacheEvict(value = "categorias", allEntries = true)
    public CategoriaIndicador guardarCategoria(CategoriaIndicador categoria) {
        return categoriaIndicadorRepository.save(categoria);
    }

    @Cacheable("indicadores")
    public List<Indicador> obtenerTodos() {
        return indicadorRepository.findAll();
    }

    @CacheEvict(value = "indicadores", allEntries = true)
    public Indicador guardar(Indicador indicador) {
        return indicadorRepository.save(indicador);
    }

    @Cacheable("valoresIndicadores")
    public List<ValorIndicador> obtenerValoresActuales() {
        return valorIndicadorRepository.findAll();
    }

    @CacheEvict(value = "valoresIndicadores", allEntries = true)
    public ValorIndicador calcularValorIndicador(Integer indicadorId, String tipo, Object... params) {
        Indicador indicador = indicadorRepository.findById(indicadorId)
                .orElseThrow(() -> new RuntimeException("Indicador no encontrado: " + indicadorId));

        CalculoStrategy strategy = calculoFactory.crearStrategy(tipo, params);
        BigDecimal resultado = strategy.calcular();

        ValorIndicador valor = new ValorIndicador();
        valor.setIndicador(indicador);
        valor.setValor(resultado);
        valor.setPeriodo(LocalDate.now().toString().substring(0, 7));
        valor.setFechaCalculo(LocalDate.now());

        return valorIndicadorRepository.save(valor);
    }

    public String generarIndicadoresPorDefecto() {
        if (categoriaIndicadorRepository.count() > 0) {
            return "Los indicadores ya fueron inicializados.";
        }

        CategoriaIndicador ventas = new CategoriaIndicador();
        ventas.setNombre("Ventas");
        ventas.setDescripcion("Indicadores del área de ventas");
        ventas = categoriaIndicadorRepository.save(ventas);

        CategoriaIndicador inventario = new CategoriaIndicador();
        inventario.setNombre("Inventario");
        inventario.setDescripcion("Indicadores de gestión de inventario");
        inventario = categoriaIndicadorRepository.save(inventario);

        CategoriaIndicador rentabilidad = new CategoriaIndicador();
        rentabilidad.setNombre("Rentabilidad");
        rentabilidad.setDescripcion("Indicadores de rentabilidad financiera");
        rentabilidad = categoriaIndicadorRepository.save(rentabilidad);

        crearIndicador("Ticket Promedio", "Ventas totales / N° transacciones", "CLP", "mensual", ventas);
        crearIndicador("Rotación de Inventario", "(Inventario actual / Inventario inicial) * 100", "%", "mensual", inventario);
        crearIndicador("Margen de Rentabilidad", "((Ingresos - Costos) / Ingresos) * 100", "%", "mensual", rentabilidad);

        return "Categorías e indicadores inicializados correctamente.";
    }

    private void crearIndicador(String nombre, String formula, String unidad, String frecuencia, CategoriaIndicador categoria) {
        Indicador ind = new Indicador();
        ind.setNombre(nombre);
        ind.setFormula(formula);
        ind.setUnidad(unidad);
        ind.setFrecuencia(frecuencia);
        ind.setCategoria(categoria);
        indicadorRepository.save(ind);
    }
}
