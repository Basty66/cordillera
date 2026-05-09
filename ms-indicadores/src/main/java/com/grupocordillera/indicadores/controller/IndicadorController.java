package com.grupocordillera.indicadores.controller;

import com.grupocordillera.indicadores.entity.CategoriaIndicador;
import com.grupocordillera.indicadores.entity.Indicador;
import com.grupocordillera.indicadores.entity.ValorIndicador;
import com.grupocordillera.indicadores.service.IndicadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/indicadores")
@RequiredArgsConstructor
@Tag(name = "Indicadores", description = "Operaciones de indicadores y KPIs")
public class IndicadorController {

    private final IndicadorService indicadorService;

    @GetMapping
    @Operation(summary = "Listar indicadores", description = "Retorna una lista de todos los indicadores KPI")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de indicadores obtenida exitosamente")
    })
    public ResponseEntity<List<Indicador>> listar() {
        return ResponseEntity.ok(indicadorService.obtenerTodos());
    }

    @PostMapping
    @Operation(summary = "Crear indicador", description = "Crea un nuevo indicador KPI en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Indicador creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<Indicador> crear(@RequestBody Indicador indicador) {
        return ResponseEntity.ok(indicadorService.guardar(indicador));
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorias", description = "Retorna una lista de todas las categorias de indicadores")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de categorias obtenida exitosamente")
    })
    public ResponseEntity<List<CategoriaIndicador>> listarCategorias() {
        return ResponseEntity.ok(indicadorService.obtenerCategorias());
    }

    @PostMapping("/categorias")
    @Operation(summary = "Crear categoria", description = "Crea una nueva categoria de indicadores")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<CategoriaIndicador> crearCategoria(@RequestBody CategoriaIndicador categoria) {
        return ResponseEntity.ok(indicadorService.guardarCategoria(categoria));
    }

    @GetMapping("/valores/actuales")
    @Operation(summary = "Valores actuales", description = "Retorna los valores actuales de todos los indicadores")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Valores obtenidos exitosamente")
    })
    public ResponseEntity<List<ValorIndicador>> valoresActuales() {
        return ResponseEntity.ok(indicadorService.obtenerValoresActuales());
    }

    @PostMapping("/calcular")
    @Operation(summary = "Calcular indicador", description = "Calcula el valor de un indicador segun el tipo de calculo y parametros")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Indicador calculado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parametros invalidos")
    })
    public ResponseEntity<ValorIndicador> calcular(
            @RequestParam Integer indicadorId,
            @RequestParam String tipo,
            @RequestParam BigDecimal param1,
            @RequestParam(required = false) BigDecimal param2) {
        if (param2 == null) param2 = BigDecimal.ZERO;
        return ResponseEntity.ok(indicadorService.calcularValorIndicador(indicadorId, tipo, param1, param2));
    }

    @PostMapping("/inicializar")
    @Operation(summary = "Inicializar indicadores", description = "Genera los indicadores por defecto del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Indicadores inicializados exitosamente")
    })
    public ResponseEntity<String> inicializar() {
        return ResponseEntity.ok(indicadorService.generarIndicadoresPorDefecto());
    }
}
