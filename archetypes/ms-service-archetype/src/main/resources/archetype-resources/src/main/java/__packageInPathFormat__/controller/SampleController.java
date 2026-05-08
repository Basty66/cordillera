package ${package}.controller;

import ${package}.entity.SampleEntity;
import ${package}.service.SampleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/samples")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @GetMapping
    public ResponseEntity<List<SampleEntity>> listar() {
        return ResponseEntity.ok(sampleService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SampleEntity> obtener(@PathVariable Long id) {
        return sampleService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SampleEntity> crear(@RequestBody SampleEntity entity) {
        return ResponseEntity.ok(sampleService.guardar(entity));
    }
}
