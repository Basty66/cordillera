package ${package}.service;

import ${package}.entity.SampleEntity;
import ${package}.repository.SampleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SampleService {

    private final SampleRepository sampleRepository;

    public SampleService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    public List<SampleEntity> listarTodos() {
        return sampleRepository.findAll();
    }

    public Optional<SampleEntity> obtenerPorId(Long id) {
        return sampleRepository.findById(id);
    }

    public SampleEntity guardar(SampleEntity entity) {
        return sampleRepository.save(entity);
    }

    public void eliminar(Long id) {
        sampleRepository.deleteById(id);
    }
}
