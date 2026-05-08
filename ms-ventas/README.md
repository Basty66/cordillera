# ms-ventas - Microservicio de Ventas

Microservicio para la gestión de ventas, productos y sucursales.

## Tecnologias

- Spring Boot 3.2.5
- Java 17
- PostgreSQL (schema `ventas`)
- Spring Data JPA
- SpringDoc OpenAPI 2.5.0

## Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/ventas | Listar ventas |
| POST | /api/ventas | Registrar venta |
| GET | /api/productos | Listar productos |
| POST | /api/productos/generar/{cantidad} | Generar productos masivos |
| GET | /api/sucursales | Listar sucursales |
| POST | /api/sucursales | Crear sucursal |
| GET | /api/reportes/resumen-ventas | Resumen de ventas |
| GET | /api/reportes/ventas-por-sucursal | Ventas agrupadas por sucursal |

## Patrones Implementados

- **Repository**: JpaRepository para cada entidad
- **Observer**: VentaRegistradaEvent + Listeners (Spring ApplicationEventPublisher)
- **Builder**: VentaBuilder para construccion de objetos Venta

## Ejecucion

```bash
mvn spring-boot:run -q
```

Puerto: 8081
