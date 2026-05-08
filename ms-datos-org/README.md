# ms-datos-org - Microservicio de Datos Organizacionales

Microservicio para la gestion de empleados y departamentos.

## Tecnologias

- Spring Boot 3.2.5
- Java 17
- PostgreSQL (schema `datos_org`)
- Spring Data JPA

## Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/empleados | Listar empleados |
| POST | /api/empleados | Crear empleado |
| POST | /api/empleados/generar/{cantidad} | Generar empleados masivos |
| GET | /api/departamentos | Listar departamentos |
| POST | /api/departamentos | Crear departamento |

## Patrones Implementados

- **Repository**: JpaRepository para cada entidad
- **Factory Method**: EmpleadoFactory para creacion de empleados segun tipo

## Ejecucion

```bash
mvn spring-boot:run -q
```

Puerto: 8082
