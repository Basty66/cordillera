# ms-indicadores - Microservicio de Indicadores KPI

Microservicio para la gestion y calculo de indicadores (KPIs) organizacionales.

## Tecnologias

- Spring Boot 3.2.5
- Java 17
- PostgreSQL (schema `indicadores`)
- Spring Data JPA
- SpringDoc OpenAPI 2.5.0

## Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/indicadores | Listar indicadores |
| POST | /api/indicadores | Crear indicador |
| GET | /api/indicadores/categorias | Listar categorias |
| POST | /api/indicadores/categorias | Crear categoria |
| GET | /api/indicadores/valores/actuales | Valores actuales |
| POST | /api/indicadores/calcular | Calcular KPI |
| POST | /api/indicadores/inicializar | Inicializar KPIs por defecto |

## Patrones Implementados

- **Repository**: JpaRepository para cada entidad
- **Strategy**: CalculoStrategy con implementaciones para VENTAS, INVENTARIO, RENTABILIDAD
- **Factory Method**: CalculoIndicadorFactory para crear la estrategia correcta segun tipo

## Ejecucion

```bash
mvn spring-boot:run -q
```

Puerto: 8083
