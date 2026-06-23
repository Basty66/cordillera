# Presentación Defensa Oral — Grupo Cordillera

## Slide 1: Portada (30s)
- **Proyecto:** Sistema de Gestión de Ventas Multisucursal
- **Arquitectura:** Microservicios + BFF + API Gateway
- **Stack:** React 19 + Spring Boot 3.2 + Java 21 + PostgreSQL (Neon)
- **Repositorio principal:** `github.com/Basty66/cordillera`

---

## Slide 2: Problemática (1 min)
> Cadena de retail chilena necesita un sistema unificado que integre ventas, datos organizacionales e indicadores de gestión, con capacidad de escalar y mantener separación de responsabilidades.

---

## Slide 3: Arquitectura de Microservicios (2 min)

**Diagrama conceptual:**

```
[Frontend React]
       |
[API Gateway] — puerto 9084
    |         |           |          |
[BFF]     [ms-ventas] [ms-datos-org] [ms-indicadores]
:9080      :8081        :8082          :8083
    |         |           |          |
    +-----[Neon PostgreSQL]---------+
```

### ¿Por qué microservicios?
- **ms-ventas:** Gestión de productos, ventas, sucursales, reportes, clima, económico
- **ms-datos-org:** Departamentos y empleados
- **ms-indicadores:** Cálculo de KPI con Strategy Pattern
- **BFF (Backend For Frontend):** Auth JWT, tickets, dashboard agregado
- **API Gateway:** Routing único, circuit breakers, fallbacks

**Patrones aplicados:**
- **BFF:** Un backend específico para las necesidades del frontend
- **API Gateway:** Punto único de entrada con balanceo y tolerancia a fallos
- **Strategy Pattern:** En ms-indicadores para cálculo de KPIs (ventas, rentabilidad, inventario)
- **Factory Pattern:** Creación de estrategias de indicadores
- **Circuit Breaker:** resilience4j en todas las llamadas BFF → microservicios

---

## Slide 4: Frontend (1.5 min)
- **Framework:** React 19 + Vite
- **UI:** Tailwind CSS 4 + Framer Motion (animaciones)
- **Gráficos:** Chart.js / Recharts
- **Estado:** React Context (AuthContext, ThemeContext)
- **Testing:** Vitest + React Testing Library — **45 tests**
- **Rutas protegidas** por rol (ADMIN, VENDEDOR, BODEGUERO)

---

## Slide 5: Backend — Componentes (1.5 min)

| Módulo | Tecnología | Puerto | Tests | Cobertura |
|--------|-----------|:------:|:-----:|:---------:|
| api-gateway | Spring Cloud Gateway | 9084 | 6 | **89%** |
| bff | Spring Boot + Security | 8090/9080 | 28 | **62%** |
| ms-ventas | Spring Boot + JPA | 8081/9081 | 86 | **91%** |
| ms-datos-org | Spring Boot + JPA | 8082/9082 | 22 | **99%** |
| ms-indicadores | Spring Boot + Strategy | 8083/9083 | 38 | **62%** |

---

## Slide 6: APIs Externas (1 min)

### 1. mindicador.cl
- Indicadores económicos chilenos: UF, USD, UTM, IPC, Euro
- **Datos reales hoy:** UF=$40.801, USD=$905,78
- Fallback: valores hardcodeados + caché 5 min

### 2. OpenWeatherMap
- Clima en tiempo real de 9 ciudades con sucursales
- **API key configurada → todo real**
- Fallback: datos simulados por ciudad

---

## Slide 7: Persistencia de Datos (1 min)
- **Base de datos:** Neon PostgreSQL (cloud, plan gratuito)
- **ORM:** JPA / Hibernate 6
- **Esquema por microservicio:**
  - `ventas.productos`, `ventas.ventas`, `ventas.sucursales`
  - `datos_org.departamentos`, `datos_org.empleados`
  - `indicadores.categorias_indicador`, `indicadores.valores_indicador`
- **BFF:** H2 en memoria (auth, tickets)
- **Conexión:** Pool HikariCP con SSL
- **Datos iniciales:** DataInitializer con ~1800 productos, 1773 ventas, 35 empleados

---

## Slide 8: Pruebas Unitarias (2 min)

### Resumen
| Componente | Tests | Fallos | Cobertura |
|-----------|:-----:|:------:|:---------:|
| ms-ventas | 86 | 0 | **91%** |
| ms-datos-org | 22 | 0 | **99%** |
| ms-indicadores | 38 | 0 | **62%** |
| bff | 28 | 0 | **62%** |
| api-gateway | 6 | 0 | **89%** |
| **Backend total** | **180** | **0** | **≥60%** ✅ |
| Frontend (Vitest) | 45 | 0 | — |
| **Gran total** | **225** | **0** | — |

### Frameworks
- **Backend:** JUnit 5 + Mockito + Spring Boot Test + JaCoCo
- **Frontend:** Vitest + React Testing Library

### Cobertura mínima 60% — TODOS cumplen

---

## Slide 9: Demo en Vivo (3 min)

1. **Abrir terminal** → ejecutar `npm test` (frontend, 45 tests)
2. **Ejecutar tests backend** → `.\mvnw.cmd test` (180 tests)
3. **Mostrar JaCoCo** → abrir `target/site/jacoco/index.html`
4. **Docker** → `docker compose ps` (6 contenedores UP)
5. **Login + endpoints** → Postman o terminal
6. **APIs externas** → `/api/economico/indicadores` + `/api/clima/sucursales`

---

## Slide 10: Versionamiento GitHub (30s)
6 repositorios separados por componente en `github.com/Basty66/`:
- `cordillera` — docs, docker-compose, README global
- `ms-ventas-frontend` — React
- `ms-ventas-bff` — BFF Spring Boot
- `ms-ventas` — microservicio ventas
- `ms-datos-org` — microservicio datos org
- `ms-indicadores` — microservicio indicadores
- `ms-ventas-gateway` — API Gateway

Release ZIP v1.0.0 subido (173 MB).

---

## Preguntas frecuentes para la defensa

### ¿Por qué microservicios y no monolito?
> Escalabilidad individual, equipos independientes, despliegues separados, tecnologías heterogéneas, aislamiento de fallos.

### ¿Por qué BFF?
> Evita que el frontend conozca la topología de microservicios. El BFF orquesta, autentica y transforma respuestas.

### ¿Por qué API Gateway?
> Punto único de entrada, routing, circuit breakers, seguridad perimetral, logging centralizado.

### ¿Cómo aseguras cobertura ≥60%?
> JaCoCo en cada `pom.xml`, `mvn clean test` genera reporte, verificamos en CI.

### ¿Qué patrones de diseño aplicaste?
> **Strategy** (cálculo de indicadores), **Factory** (creación de estrategias), **BFF**, **API Gateway**, **Circuit Breaker** (resilience4j), **DTO**, **Builder** (VentaBuilder).

### ¿Cómo persistes los datos?
> JPA/Hibernate con PostgreSQL Neon cloud. Cada microservicio tiene su esquema. BFF usa H2 para auth y tickets.

### ¿Escalabilidad?
> Cada microservicio escala independientemente. Gateway hace routing. BFF puede tener múltiples instancias.
