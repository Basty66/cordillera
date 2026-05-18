# Presentación Grupo Cordillera
## Plataforma de Monitoreo Inteligente

---

# Diapositiva 1 — Portada

**Grupo Cordillera**
Plataforma de Monitoreo Inteligente

Arquitectura de Microservicios con Spring Boot + React

Desarrollo Fullstack III — Duoc UC
Cristian Cerda · Gonzalo Berríos · Jaime Manzo

---

# Diapositiva 2 — Problemáticas que Resolvemos

- **Silos de información** — Sistemas independientes (POS, e-commerce, inventarios, finanzas, atención al cliente) sin integración eficiente
- **Procesos manuales** — Consolidación de datos hecha a mano, con alta probabilidad de error humano
- **Datos desactualizados** — Sin acceso a información en tiempo real sobre ventas, inventario y desempeño
- **Escalabilidad limitada** — Sistemas anteriores difíciles de escalar ante el crecimiento del negocio
- **Latencia alta** — Consultas a BD remota (Neon.tech) de hasta ~10 segundos en frío
- **Toma de decisiones riesgosa** — Decisiones basadas en información desactualizada o incompleta
- **Sin plataforma unificada** — No existía un único lugar donde centralizar la información del negocio

---

# Diapositiva 3 — Arquitectura del Sistema

## ¿Por qué microservicios en vez de monolito?

**Estructura:**
```
Frontend React 19 + Vite 8 :5173
        │
API Gateway (Spring Cloud Gateway) :8084
        │
┌───────┼───────────────┐
BFF    ms-ventas   ms-datos-org   ms-indicadores
:8090  :8081        :8082          :8083
```

**Razones:**
- **Desacoplamiento funcional** — Cada servicio tiene su schema y responsabilidad
- **Escalabilidad independiente** — Escalar solo el servicio necesario
- **Tolerancia a fallos aislada** — Si cae ms-ventas, los demás siguen funcionando
- **Equipos paralelos** — Múltiples devs trabajando simultáneamente (9 ramas Git)
- **Despliegue independiente** — Cada servicio con su propio Dockerfile
- **Mantenibilidad** — Código más pequeño, enfocado y testeable

| Servicio | Puerto | Schema/BD | Rol |
|----------|--------|-----------|-----|
| api-gateway | 8084 | — | Enrutamiento + Circuit Breaker |
| bff | 8090 | H2 (memoria) | BFF + Auth + Tickets + Reportes |
| ms-ventas | 8081 | ventas (PostgreSQL) | Ventas, productos, sucursales |
| ms-datos-org | 8082 | datos_org (PostgreSQL) | Empleados, departamentos |
| ms-indicadores | 8083 | indicadores (PostgreSQL) | KPIs, categorías, valores |

---

# Diapositiva 4 — Stack Tecnológico

| Tecnología | ¿Por qué? |
|------------|-----------|
| **Spring Boot 3.2 + Java 21** | Tipado fuerte que reduce errores en compilación; ecosistema maduro para microservicios (Cloud Gateway, Circuit Breaker, JPA); rendimiento superior en I/O; integración nativa con arquetipos Maven |
| **PostgreSQL (Neon.tech)** | Una sola instancia con 3 schemas (ventas, datos_org, indicadores) mantiene aislamiento lógico pero simplifica operación y reduce costos vs. BDs separadas |
| **Cache en memoria** (ConcurrentMapCacheManager) | Suficiente para volumen actual (~1772 ventas, 60 productos, 35 empleados); reduce latencia de ~10s a <70ms; sin infraestructura adicional como Redis |
| **React 19 + Vite 8 + Tailwind CSS v4** | Moderno, rápido con HMR, estilizado utilitario, amplio ecosistema y comunidad |
| **Resilience4j (Circuit Breaker)** | Ligero y nativo de Spring Cloud Gateway + BFF; protege contra fallos en cascada con fallbacks automáticos |
| **Docker Compose** | Despliegue reproducible con 6 contenedores (5 backend + 1 frontend Nginx); sin dependencia cloud de pago |

---

# Diapositiva 5 — Patrones de Diseño

| Patrón | Problema que Resuelve | SOLID | Ubicación |
|--------|----------------------|-------|-----------|
| **Repository** (JPA) | Desacopla lógica de negocio de la persistencia | SRP | Todos los módulos |
| **Factory Method** | Centraliza creación de objetos complejos | OCP | ms-datos-org (EmpleadoFactory), ms-indicadores (CalculoIndicadorFactory) |
| **Strategy** | Algoritmos intercambiables de KPI en runtime | OCP | ms-indicadores (3 impls: VENTAS, INVENTARIO, RENTABILIDAD) |
| **Observer** (Eventos) | Múltiples reacciones a venta sin acoplar | DIP | ms-ventas (VentaRegistradaEvent + listeners) |
| **Builder** | Construcción paso a paso de Venta con validación | SRP | ms-ventas (VentaBuilder) |
| **Circuit Breaker** | Evita fallos en cascada con fallbacks | — | api-gateway, bff |
| **BFF** | Agrega datos de 3+ servicios para el frontend | ISP | bff (DashboardService) |
| **API Gateway** | Punto único de entrada con enrutamiento y CORS | — | api-gateway |
| **Custom Hook** (React) | Abstracción de data fetching reutilizable | — | frontend/hooks/useApi.js |
| **Provider** (React) | Gestión de estado de autenticación JWT global | — | frontend/context/AuthContext.jsx |
| **Cache-Aside** (@Cacheable) | Reduce latencia de ~10s a <70ms | — | Todos los servicios |

---

# Diapositiva 6 — Arquetipos Maven

## ¿Para qué sirven los arquetipos?

Los arquetipos Maven son plantillas de proyecto que permiten generar nuevos módulos con la misma estructura, configuraciones y dependencias predefinidas. Garantizan que todos los microservicios sigan el mismo estándar.

### ms-service-archetype
**Propósito:** Generar microservicios Spring Boot con JPA + PostgreSQL

Estructura generada:
```
src/main/java/com/grupocordillera/{artifactId}/
  ├── Application.java
  ├── controller/SampleController.java
  ├── entity/SampleEntity.java
  ├── repository/SampleRepository.java
  ├── service/SampleService.java
  └── exception/GlobalExceptionHandler.java
src/main/resources/
  ├── application.properties
  └── schema.sql
```
Preconfigurado con: Spring Web, Data JPA, Validation, PostgreSQL Driver, Lombok, Test

### bff-archetype
**Propósito:** Generar módulos BFF con Spring Boot + Security + JWT + Resilience4j + H2

Estructura generada:
```
src/main/java/com/grupocordillera/{artifactId}/
  ├── BffApplication.java
  ├── config/DataInitializer.java, RestTemplateConfig.java
  ├── controller/SampleController.java
  ├── dto/LoginRequest.java, LoginResponse.java
  ├── entity/Usuario.java
  ├── exception/GlobalExceptionHandler.java
  ├── repository/UsuarioRepository.java
  ├── security/JwtAuthFilter.java, JwtUtil.java, SecurityConfig.java
  └── service/SampleService.java
```
Preconfigurado con: Spring Security, JWT (jjwt 0.12.6), Resilience4j, H2, Lombok, Test

**Beneficio:** Tiempo de bootstrap reducido, estructura estandarizada entre todos los módulos, nuevos desarrolladores se integran más rápido al equipo.

---

# Diapositiva 7 — Modelos de Datos

### ms-ventas (schema: ventas)
| Entidad | Atributos Clave |
|---------|----------------|
| Venta | id, fechaVenta, montoTotal, sucursal (FK), usuario, detalles (1:N) |
| DetalleVenta | id, venta (FK), producto (FK), cantidad, precioUnitario, subtotal |
| Producto | id, nombre, descripción, precio, stock, categoria, imagen |
| Sucursal | id, nombre, dirección, ciudad, activa |

### ms-datos-org (schema: datos_org)
| Entidad | Atributos Clave |
|---------|----------------|
| Empleado | id, nombre, apellido, email, cargo, departamento (FK, N:1) |
| Departamento | id, nombre, descripción, activo |

### ms-indicadores (schema: indicadores)
| Entidad | Atributos Clave |
|---------|----------------|
| Indicador | id, nombre, descripción, tipo (VENTAS/INVENTARIO/RENTABILIDAD), categoria (FK) |
| ValorIndicador | id, indicador (FK), valor, fechaCálculo, período |
| Categoria | id, nombre, descripción |

### bff (H2 en memoria)
| Entidad | Atributos Clave |
|---------|----------------|
| Usuario | id, username, password (hasheada), email, rol (ADMIN/VENDEDOR/BODEGA) |
| Ticket | id, título, descripción, estado (ABIERTO/EN_PROGRESO/RESUELTO/CERRADO), prioridad, usuario (FK) |

**Estrategia de BD:** PostgreSQL único con 3 schemas separados (ventas, datos_org, indicadores). Mantiene aislamiento lógico entre dominios pero simplifica operación, reduce costos de conexión y facilita backups. Hibernate gestiona el DDL automáticamente con `ddl-auto=update` y los schemas se crean vía schema.sql.

---

# Diapositiva 8 — Optimizaciones de Rendimiento

| Optimización | Antes | Después |
|-------------|-------|---------|
| Cache en memoria (frío → caliente) | ~10,000 ms (consulta fría Neon.tech) | **< 70 ms** en todos los endpoints |
| Paginación de ventas | Carga completa ~1772 registros | 20 registros por página con Page\<Venta\> |
| Eliminación N+1 queries | Múltiples queries por entidad relacionada | JOIN FETCH + @EntityGraph en todas las relaciones |
| Paralelización en BFF | 7 llamadas secuenciales a microservicios | 7 llamadas paralelas con CompletableFuture + ThreadPoolTaskExecutor |
| Pool de conexiones HTTP | Conexiones por request | Pool de 50 conexiones (10 por ruta) con timeout 5s/10s |
| Índices de BD (covering indexes) | Full table scans | Índices en fecha_venta, sucursal_id, producto_id, compuestos |

---

# Diapositiva 9 — Credenciales de Prueba & API

## Credenciales
| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| admin | admin123 | ADMIN |
| vendedor | ventas123 | VENDEDOR |
| bodega | bodega123 | BODEGA |
| carla | carla123 | VENDEDOR |
| ana | ana123 | ADMIN |
| luis | luis123 | VENDEDOR |

## API Endpoints Principales
| Endpoint | Descripción |
|----------|-------------|
| POST /api/auth/login | Inicio de sesión (JWT) |
| GET /api/bff/dashboard | Dashboard consolidado |
| GET /api/ventas | Ventas paginadas |
| GET /api/productos | Productos con imágenes |
| GET /api/empleados | Empleados |
| GET /api/indicadores/valores/actuales | KPIs y métricas |
| GET/POST /api/tickets | Gestión de tickets |

---

# Diapositiva 10 — Cierre

**¡Gracias!**

Repositorio: https://github.com/Basty66/cordillera
Frontend: http://localhost:5173
API Gateway: http://localhost:8084

Versión 1.0.1
Desarrollo Fullstack III — Duoc UC

**Grupo Cordillera**
