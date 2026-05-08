# BFF - Backend For Frontend (Grupo Cordillera)

Backend For Frontend que agrega datos de los microservicios internos y gestiona autenticacion.

## Tecnologias

- Spring Boot 3.2.5
- Java 17
- Spring Security + JWT (jjwt 0.12.6)
- H2 (base authdb)
- Resilience4j (Circuit Breaker)
- Spring Actuator

## Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/auth/login | Iniciar sesion (JWT) |
| POST | /api/auth/register | Registrar usuario |
| GET | /api/auth/usuarios | Listar usuarios (ADMIN) |
| GET | /api/bff/dashboard | Dashboard consolidado |
| GET/POST | /api/tickets | CRUD tickets |
| GET | /api/reportes/dashboard | Reportes del sistema |

## Patrones Implementados

- **BFF (Backend For Frontend)**: Agregacion de datos de multiples microservicios
- **Circuit Breaker**: Resilience4j para tolerancia a fallos
- **Repository**: JPA repositories para usuarios y tickets
- **Singleton**: JwtUtil como componente Spring

## Ejecucion

```bash
mvn spring-boot:run -q
```

Puerto: 8090
