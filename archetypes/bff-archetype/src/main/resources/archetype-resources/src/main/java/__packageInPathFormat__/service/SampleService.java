package ${package}.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SampleService {

    public Map<String, Object> getDashboardData() {
        return Map.of(
                "service", "${artifactId}",
                "status", "running",
                "version", "${version}"
        );
    }
}
