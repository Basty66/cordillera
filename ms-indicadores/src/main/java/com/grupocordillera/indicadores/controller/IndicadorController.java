package com.grupocordillera.indicadores.controller;

import com.grupocordillera.indicadores.entity.CategoriaIndicador;
import com.grupocordillera.indicadores.entity.Indicador;
import com.grupocordillera.indicadores.entity.ValorIndicador;
import com.grupocordillera.indicadores.service.IndicadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/indicadores")
@RequiredArgsConstructor
public class IndicadorController {

    private final IndicadorService indicadorService;

    @GetMapping
    public ResponseEntity<List<Indicador>> listar() {
        return ResponseEntity.ok(indicadorService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<Indicador> crear(@RequestBody Indicador indicador) {
        return ResponseEntity.ok(indicadorService.guardar(indicador));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaIndicador>> listarCategorias() {
        return ResponseEntity.ok(indicadorService.obtenerCategorias());
    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaIndicador> crearCategoria(@RequestBody CategoriaIndicador categoria) {
        return ResponseEntity.ok(indicadorService.guardarCategoria(categoria));
    }

    @GetMapping("/valores/actuales")
    public ResponseEntity<List<ValorIndicador>> valoresActuales() {
        return ResponseEntity.ok(indicadorService.obtenerValoresActuales());
    }

    @PostMapping("/calcular")
    public ResponseEntity<ValorIndicador> calcular(
            @RequestParam Integer indicadorId,
            @RequestParam String tipo,
            @RequestParam BigDecimal param1,
            @RequestParam(required = false) BigDecimal param2) {
        if (param2 == null) param2 = BigDecimal.ZERO;
        return ResponseEntity.ok(indicadorService.calcularValorIndicador(indicadorId, tipo, param1, param2));
    }

    @PostMapping("/inicializar")
    public ResponseEntity<String> inicializar() {
        return ResponseEntity.ok(indicadorService.generarIndicadoresPorDefecto());
    }
}
