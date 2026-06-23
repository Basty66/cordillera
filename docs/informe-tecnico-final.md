# Informe Técnico Final — Grupo Cordillera

## Sistema de Gestión de Ventas Multisucursal

**Asignatura:** DSY1106 — Desarrollo Fullstack III
**Evaluación:** Parcial N°3 — Integración de Arquitectura de Microservicios
**Estudiante:** [Nombre]
**Fecha:** Junio 2026

---

## 1. Esquema de Arquitectura de Microservicios

### Diagrama de Arquitectura

```
┌──────────────────────────────────────────────────────────┐
│                     Cliente Web                          │
│              React 19 + Vite + Tailwind                  │
│                    localhost:5173                        │
└────────────────────────┬─────────────────────────────────┘
                         │ HTTP
┌────────────────────────▼─────────────────────────────────┐
│                  API Gateway (9084)                      │
│        Spring Cloud Gateway + Circuit Breaker            │
│           Rutas: /api/ventas/**, /api/tickets/**, etc.   │
└──┬──────────────┬──────────────┬──────────────┬──────────┘
   │              │              │              │
   ▼              ▼              ▼              ▼
┌────────┐ ┌────────────┐ ┌────────────┐ ┌──────────────┐
│  BFF   │ │ ms-ventas  │ │ms-datos-org│ │ms-indicadores│
│ :8090  │ │  :8081     │ │  :8082     │ │  :8083      │
├────────┤ ├────────────┤ ├────────────┤ ├──────────────┤
│ Auth   │ │ Productos  │ │Deptos.     │ │Strategy      │
│ JWT    │ │ Ventas     │ │Empleados   │ │Factory       │
│ Tickets│ │ Sucursales │ │            │ │Calculo KPIs  │
│Dashbrd │ │ Reportes   │ │            │ │              │
│ H2 mem │ │ Clima/Econ │ │            │ │              │
└───┬────┘ └──────┬─────┘ └──────┬─────┘ └──────┬───────┘
    │             │              │              │
    └─────────────┴──────┬───────┴──────────────┘
                         │
              ┌──────────▼──────────┐
              │   Neon PostgreSQL   │
              │  (Cloud - AWS us)   │
              └─────────────────────┘
```

### Componentes

| Componente | Rol | Tecnología | Puerto Host |
|-----------|-----|-----------|:-----------:|
| Frontend | Interfaz de usuario | React 19 + Vite + Tailwind | 5173 |
| API Gateway | Routing único, circuit breakers | Spring Cloud Gateway 4 | 9084 |
| BFF | Auth, tickets, dashboard | Spring Boot 3.2 + Security | 9080 |
| ms-ventas | Ventas, productos, sucursales, reportes, APIs externas | Spring Boot + JPA | 9081 |
| ms-datos-org | Departamentos, empleados | Spring Boot + JPA | 9082 |
| ms-indicadores | Cálculo de KPI (Strategy Pattern) | Spring Boot + JPA | 9083 |

### Principios de la Arquitectura

1. **Separación de responsabilidades:** Cada microservicio gestiona un dominio específico
2. **Comunicación síncrona via REST:** Gateway expone API unificada, cada servicio tiene su propia API
3. **Tolerancia a fallos:** Circuit Breaker (resilience4j) en todas las llamadas BFF → microservicios
4. **Seguridad perimetral:** JWT validado en BFF, Gateway filtra rutas públicas vs protegidas
5. **Persistencia independiente:** Cada microservicio tiene su esquema en la misma BD Neon

---

## 2. Persistencia de Datos

### Estrategia
- **ORM:** JPA / Hibernate 6 con `ddl-auto: update`
- **Base de datos:** Neon PostgreSQL (cloud, plan gratuito)
- **Pool de conexiones:** HikariCP con SSL habilitado
- **BFF:** H2 en memoria (`ddl-auto: create-drop`) para autenticación y tickets

### Esquemas por Microservicio

#### ms-ventas (esquema `ventas`)
| Tabla | Descripción |
|-------|-------------|
| `productos` | Catálogo con precio, stock, categoría, imagen |
| `ventas` | Transacciones con total, sucursal, fecha |
| `venta_productos` | Detalle ítems por venta |
| `sucursales` | Sucursales con dirección, ciudad, imagen |

#### ms-datos-org (esquema `datos_org`)
| Tabla | Descripción |
|-------|-------------|
| `departamentos` | Departamentos con nombre y descripción |
| `empleados` | Empleados con cargo, salario, departamento |

#### ms-indicadores (esquema `indicadores`)
| Tabla | Descripción |
|-------|-------------|
| `categorias_indicador` | Categorías de KPI (Strategy Pattern) |
| `valores_indicador` | Valores históricos de indicadores |

### Data Initializer
- Cada microservicio tiene un `DataInitializer` que carga datos semilla
- Verifica si los datos ya existen antes de insertar (evita duplicados en cada restart)
- Carga: ~1800 productos, 1773 ventas históricas, 35 empleados, 8 departamentos, 12 sucursales, 3 categorías de indicadores

---

## 3. Pruebas Unitarias

### 3.1. Resumen Global

| Componente | Framework | Tests | Fallos | Cobertura JaCoCo |
|-----------|-----------|:-----:|:------:|:----------------:|
| ms-ventas | JUnit 5 + Mockito + Spring Boot Test | 86 | 0 | **91%** |
| ms-datos-org | JUnit 5 + Mockito + Spring Boot Test | 22 | 0 | **99%** |
| ms-indicadores | JUnit 5 + Mockito + Spring Boot Test | 38 | 0 | **62%** |
| bff | JUnit 5 + Mockito + Spring Boot Test | 28 | 0 | **62%** |
| api-gateway | JUnit 5 + Spring Boot Test | 6 | 0 | **89%** |
| **Backend total** | | **180** | **0** | **≥60% CUMPLE** |
| Frontend | Vitest + React Testing Library | 45 | 0 | — |
| **Gran total** | | **225** | **0** | — |

### 3.2. Cobertura por Módulo (JaCoCo)

```
ms-ventas      ████████████████████░░  91%
ms-datos-org   ██████████████████████  99%
api-gateway    ████████████████████░░  89%
bff            ██████████████░░░░░░░░  62%
ms-indicadores ██████████████░░░░░░░░  62%
               ──────────────────────
Mínimo exigido:                    60%
```

**Todos los módulos cumplen con la cobertura mínima del 60% exigida.**

### 3.3. Tipos de Pruebas Implementadas

| Tipo | Ejemplo | Cobertura |
|------|---------|-----------|
| **Unitarias (Service)** | `ProductoServiceTest` (11 tests), `VentaServiceTest` (9 tests) | Lógica de negocio |
| **Integración (Controller)** | `ReporteControllerTest` (8 tests), `IndicadorControllerTest` (8 tests) | API REST |
| **Repositorio** | `VentaRepositoryImplTest` (8 tests) | Consultas personalizadas |
| **Eventos** | `VentaEventListenerTest`, `StockUpdateListenerTest` | Event-driven |
| **Seguridad** | `JwtUtilTest` (5 tests) | Generación/validación JWT |
| **Frontend** | Componentes, hooks, contexto, páginas | UI/UX |

### 3.4. Patrones de Diseño Aplicados en Pruebas

- **Mockito** para aislar servicios y repositorios
- **@WebMvcTest** para pruebas de controladores
- **@DataJpaTest** para pruebas de repositorios
- **MockMvc** para simular peticiones HTTP
- **Testcontainers** (cuando aplica) para pruebas de integración con BD real
- **Vitest + Testing Library** para pruebas de componentes React

### 3.5. Cómo Ejecutar las Pruebas

```bash
# Backend (cada módulo)
cd ms-ventas
.\mvnw.cmd clean test

# Todos los módulos (uno por uno)
cd ms-ventas; .\mvnw.cmd clean test
cd ..\ms-datos-org; .\mvnw.cmd clean test
cd ..\ms-indicadores; .\mvnw.cmd clean test
cd ..\bff; .\mvnw.cmd clean test
cd ..\api-gateway; .\mvnw.cmd clean test

# Frontend
cd frontend
npm test

# Generar reporte de cobertura
# Se genera automáticamente con mvn clean test en:
# target/site/jacoco/index.html
```

### 3.6. Reportes de Cobertura

Los reportes HTML de JaCoCo se generan automáticamente y están disponibles en:

```
ms-ventas/target/site/jacoco/index.html
ms-datos-org/target/site/jacoco/index.html
ms-indicadores/target/site/jacoco/index.html
bff/target/site/jacoco/index.html
api-gateway/target/site/jacoco/index.html
```

---

## 4. APIs Externas Integradas

### 4.1. mindicador.cl
- **URL:** `https://mindicador.cl/api`
- **Datos:** UF, USD, UTM, IPC, Euro (tiempo real)
- **Estado:** ✅ Funcionando con datos reales
- **Protección:** try-catch + fallback hardcodeado + `@Cacheable` 5 min

### 4.2. OpenWeatherMap
- **URL:** `https://api.openweathermap.org/data/2.5/weather?q={city},CL&appid=...&units=metric`
- **Datos:** Temperatura, humedad, viento, descripción para 9 ciudades
- **Estado:** ✅ Funcionando con datos reales (9/9 ciudades)
- **Protección:** try-catch + fallback simulado + `@Cacheable` 10 min + API key por variable env

### 4.3. Circuit Breaker (resilience4j)
- Implementado en todas las llamadas BFF → microservicios
- Ventana deslizante de 5 llamadas, umbral 50% fallo, espera 10s en estado abierto

---

## 5. Endpoints de la API REST

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login con username/password, retorna JWT |

### ms-ventas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/productos` | Lista paginada de productos |
| POST | `/api/ventas` | Crear venta |
| GET | `/api/ventas` | Ventas paginadas |
| GET | `/api/sucursales` | Lista sucursales |
| GET | `/api/reportes/resumen-ventas` | Resumen ventas |
| GET | `/api/reportes/ventas-mensuales` | Ventas por mes |
| GET | `/api/reportes/ventas-por-categoria` | Ventas por categoría |
| GET | `/api/reportes/top-productos` | Ranking productos |
| GET | `/api/reportes/ventas-por-sucursal` | Ventas por sucursal |
| GET | `/api/clima/sucursales` | Clima por sucursal |
| GET | `/api/economico/indicadores` | UF, USD, UTM, IPC |

### ms-datos-org
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/departamentos` | Lista departamentos |
| GET | `/api/empleados` | Lista empleados |

### ms-indicadores
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/indicadores` | Lista indicadores |
| GET | `/api/indicadores/categorias` | Categorías de indicadores |

### BFF
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/tickets` | Tickets de soporte |
| POST | `/api/tickets` | Crear ticket (clasificación IA) |
| GET | `/api/bff/dashboard` | Dashboard consolidado |

La colección Postman completa está en `docs/grupo-cordillera.postman_collection.json` (58 endpoints).

---

## 6. Repositorios GitHub

### Repositorio Principal
- **URL:** `https://github.com/Basty66/cordillera`
- **Contenido:** Documentación, docker-compose, README global, este informe

### Repositorio Frontend
- **URL:** `https://github.com/Basty66/ms-ventas-frontend`
- **Tecnología:** React 19 + Vite + Tailwind
- **Tests:** Vitest, 45 tests

### BFF (Backend For Frontend)
- **URL:** `https://github.com/Basty66/ms-ventas-bff`
- **Tecnología:** Spring Boot 3.2 + Security + JWT
- **Tests:** JUnit 5, 28 tests, cobertura 62%

### Microservicio Ventas
- **URL:** `https://github.com/Basty66/ms-ventas`
- **Tecnología:** Spring Boot 3.2 + JPA + PostgreSQL
- **Tests:** JUnit 5, 86 tests, cobertura 91%

### Microservicio Datos Organizacionales
- **URL:** `https://github.com/Basty66/ms-datos-org`
- **Tecnología:** Spring Boot 3.2 + JPA + PostgreSQL
- **Tests:** JUnit 5, 22 tests, cobertura 99%

### Microservicio Indicadores
- **URL:** `https://github.com/Basty66/ms-indicadores`
- **Tecnología:** Spring Boot 3.2 + Strategy Pattern
- **Tests:** JUnit 5, 38 tests, cobertura 62%

### API Gateway
- **URL:** `https://github.com/Basty66/ms-ventas-gateway`
- **Tecnología:** Spring Cloud Gateway 4
- **Tests:** JUnit 5, 6 tests, cobertura 89%

### Release
- **URL:** `https://github.com/Basty66/cordillera/releases/tag/v1.0.0`
- **Archivo:** `ms-ventas-v1.0.0.zip` (173 MB)

---

## 7. Instrucciones de Instalación y Ejecución

### Prerrequisitos
- Docker Desktop (o Docker Engine + Docker Compose)
- Node.js 18+
- Java 21+ (solo para desarrollo local)
- Git

### Ejecución con Docker (recomendada)

```bash
git clone https://github.com/Basty66/cordillera.git
cd cordillera
docker compose up -d
```

Los servicios estarán disponibles en:
- Frontend: `http://localhost:5173`
- API Gateway: `http://localhost:9084`
- BFF directo: `http://localhost:9080`

### Ejecución Local (Backend)

```bash
# Cada módulo tiene su propio mvnw
cd ms-ventas
.\mvnw.cmd spring-boot:run
```

### Ejecución Local (Frontend)

```bash
cd frontend
npm install
npm run dev
```

---

## 8. Conclusiones

1. **Arquitectura de microservicios implementada correctamente** con BFF, API Gateway y 3 microservicios de dominio.
2. **Persistencia de datos** asegurada mediante JPA/Hibernate con PostgreSQL cloud (Neon).
3. **Cobertura de pruebas ≥60%** en todos los módulos backend (máximo 99%, mínimo 62%).
4. **225 pruebas unitarias** (180 backend + 45 frontend) con 0 fallos.
5. **3 APIs externas** integradas: mindicador.cl, OpenWeatherMap, Circuit Breaker resilience4j.
6. **6 repositorios GitHub** separados por componente + release ZIP.
7. **Docker Compose** con 6 contenedores, puertos serie 908x.
8. **Colección Postman** con 58 endpoints para probar la API REST.
