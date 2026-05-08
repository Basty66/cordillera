# API Gateway - Grupo Cordillera

API Gateway basado en Spring Cloud Gateway con Resilience4j para tolerancia a fallos.

## Tecnologias

- Spring Cloud Gateway 2023.0.3
- Spring Boot 3.2.5
- Resilience4j (Circuit Breaker + TimeLimiter + Retry)
- Spring Boot Actuator

## Rutas

| Ruta | Destino | Circuit Breaker |
|------|---------|-----------------|
| /api/ventas/**, /api/productos/**, /api/sucursales/** | ms-ventas:8081 | ms-ventas-cb |
| /api/empleados/**, /api/departamentos/** | ms-datos-org:8082 | ms-datos-org-cb |
| /api/indicadores/** | ms-indicadores:8083 | ms-indicadores-cb |
| /api/auth/**, /api/tickets/**, /api/reportes/** | bff:8090 | - |
| /api/bff/** | bff:8090 | bff-cb |

## Patrones Implementados

- **API Gateway**: Punto unico de entrada
- **Circuit Breaker**: Resilience4j con fallbacks

## Ejecucion

```bash
mvn spring-boot:run -q
```

Puerto: 8084
