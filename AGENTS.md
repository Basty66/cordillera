# 📊 Grupo Cordillera — Plataforma de Monitoreo Inteligente

## 📋 Descripción General

Sistema de monitoreo del desempeño organizacional para **Grupo Cordillera**, empresa de retail y comercialización con múltiples sucursales a nivel nacional. La plataforma consolida información de diversos sistemas (punto de venta, e-commerce, inventarios, gestión financiera, atención al cliente) en una solución unificada basada en microservicios.

## 🏗️ Arquitectura del Sistema

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (React 19)                    │
│                  Vite 8 + Tailwind CSS v4                     │
│                  localhost:5173                                │
└──────────────────────────┬────────────────────────────────────┘
                           │ HTTP / API REST
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (Spring Cloud Gateway)         │
│                    Puerto: 8084                               │
│                    Circuit Breaker + TimeLimiter              │
└──┬──────────┬──────────┬──────────┬──────────┬───────────────┘
   │          │          │          │          │
   ▼          ▼          ▼          ▼          ▼
┌──────┐ ┌────────┐ ┌──────────┐ ┌────────┐ ┌──────────────┐
│ BFF  │ │ Auth   │ │ Tickets  │ │Reportes│ │ ms-ventas    │
│:8090 │ │:8090   │ │:8090     │ │:8090   │ │:8081         │
└──┬───┘ └────────┘ └──────────┘ └────────┘ └──────┬───────┘
   │                                                 │
   ▼                                                 ▼
┌──────────┐                                    ┌──────────┐
│ ms-org   │                                    │ ms-indic │
│:8082     │                                    │:8083     │
└──────────┘                                    └──────────┘
```

### Microservicios

| Servicio | Puerto | Base de Datos | Schema | Responsabilidad |
|----------|--------|---------------|--------|-----------------|
| **ms-ventas** | 8081 | PostgreSQL (Neon.tech) | `ventas` | Gestión de ventas, productos, sucursales |
| **ms-datos-org** | 8082 | PostgreSQL (Neon.tech) | `datos_org` | Empleados, departamentos, estructura organizacional |
| **ms-indicadores** | 8083 | PostgreSQL (Neon.tech) | `indicadores` | KPIs, categorías, valores de indicadores |
| **bff** | 8090 | H2 (memoria) | — | BFF + Auth + Tickets + Reportes |
| **api-gateway** | 8084 | — | — | Enrutamiento, Circuit Breaker |

### Patrones de Diseño Implementados

| Patrón | Ubicación | Propósito |
|--------|-----------|-----------|
| **Repository** | Todos los módulos (JPA Repository) | Abstracción de persistencia de datos |
| **Factory Method** | `ms-datos-org`: `EmpleadoFactory` | Creación de empleados según tipo |
| **Factory Method** | `ms-indicadores`: `CalculoIndicadorFactory` | Cálculo de KPIs según estrategia (VENTAS/INVENTARIO/RENTABILIDAD) |
| **Strategy** | `ms-indicadores`: `CalculoStrategy` + 3 impls | Algoritmos intercambiables para KPIs |
| **Observer** | `ms-ventas`: `VentaRegistradaEvent` + Listeners | Notificación de eventos de venta |
| **Builder** | `ms-ventas`: `VentaBuilder` | Construcción paso a paso de objetos Venta |
| **Circuit Breaker** | `api-gateway` + `bff` (Resilience4j) | Tolerancia a fallos en comunicación entre servicios |
| **BFF (Backend For Frontend)** | `bff/` | Agregación de datos para el frontend |
| **API Gateway** | `api-gateway/` | Punto único de entrada, enrutamiento |
| **Custom Hook** | `frontend/src/hooks/useApi.js` | Abstracción de data fetching en React |

### Flujo de Datos

1. El usuario se autentica via `POST /api/auth/login` → recibe JWT
2. Cada request del frontend incluye `Authorization: Bearer <token>`
3. API Gateway valida la ruta y aplica Circuit Breaker
4. BFF agrega datos de los 3 microservicios internos
5. Cada microservicio accede a su schema en PostgreSQL

## 🚀 Instalación y Ejecución

### Requisitos

- Java 21+
- Node.js 20+
- Maven (o usar `mvnw.cmd` / `mvnw`)
- PostgreSQL (o usar Neon.tech remoto)

### Backend

```bash
# Iniciar microservicios (cada uno en su terminal)
cd ms-ventas && .\mvnw.cmd spring-boot:run -q    # Puerto 8081
cd ms-datos-org && .\mvnw.cmd spring-boot:run -q  # Puerto 8082
cd ms-indicadores && .\mvnw.cmd spring-boot:run -q # Puerto 8083
cd bff && .\mvnw.cmd spring-boot:run -q           # Puerto 8090
cd api-gateway && .\mvnw.cmd spring-boot:run -q   # Puerto 8084
```

### Frontend

```bash
cd frontend
npm install
npm run dev  # Puerto 5173
```

### Pruebas

```bash
# Ejecutar pruebas de todos los módulos
cd ms-ventas && .\mvnw.cmd test
cd ms-datos-org && .\mvnw.cmd test
cd ms-indicadores && .\mvnw.cmd test
cd bff && .\mvnw.cmd test
cd api-gateway && .\mvnw.cmd test
```

## 🔐 Credenciales de Prueba

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `admin123` | ADMIN |
| `vendedor` | `ventas123` | VENDEDOR |
| `bodega` | `bodega123` | BODEGA |
| `carla` | `carla123` | VENDEDOR |
| `pedro` | `pedro123` | BODEGA |
| `ana` | `ana123` | ADMIN |
| `luis` | `luis123` | VENDEDOR |

## 📡 API Endpoints

### Autenticación
- `POST /api/auth/login` — Iniciar sesión (devuelve JWT)
- `POST /api/auth/register` — Registrar nuevo usuario
- `GET /api/auth/usuarios` — Listar usuarios (solo ADMIN)

### Dashboard (BFF)
- `GET /api/bff/dashboard` — Dashboard consolidado

### Tickets
- `GET /api/tickets` — Listar tickets
- `POST /api/tickets` — Crear ticket
- `GET /api/tickets/{id}` — Obtener ticket
- `PUT /api/tickets/{id}/status` — Actualizar estado
- `PUT /api/tickets/{id}` — Actualizar ticket
- `DELETE /api/tickets/{id}` — Eliminar ticket

### Reportes
- `GET /api/reportes/dashboard` — Estadísticas del sistema
- `GET /api/reportes/tickets` — Exportar tickets

### Ventas (ms-ventas)
- `GET /api/ventas` — Listar ventas
- `GET /api/productos` — Listar productos
- `GET /api/sucursales` — Listar sucursales

### Datos Org (ms-datos-org)
- `GET /api/empleados` — Listar empleados
- `GET /api/departamentos` — Listar departamentos

### Indicadores (ms-indicadores)
- `GET /api/indicadores` — Listar indicadores
- `GET /api/indicadores/valores/actuales` — Valores actuales
- `GET /api/indicadores/categorias` — Categorías
- `POST /api/indicadores/inicializar` — Inicializar KPIs

## 🧪 Estrategia de Pruebas

- **Tests Unitarios**: JUnit 5 + Mockito para servicios y factories
- **Pruebas de Integración**: Spring Boot Test para controladores
- **Cobertura Mínima**: 60% (validable con SonarQube)

## 📦 Estructura del Proyecto

```
ms-ventas/
├── api-gateway/         # Spring Cloud Gateway + Resilience4j
│   └── src/main/java/com/grupocordillera/gateway/
│       ├── config/      # RouteConfig (rutas + circuit breaker)
│       └── controller/  # FallbackController
├── bff/                 # Backend For Frontend + Auth
│   └── src/main/java/com/grupocordillera/bff/
│       ├── config/      # DataInitializer, RestTemplateConfig
│       ├── controller/  # Dashboard, Auth, Ticket, Report
│       ├── dto/         # Data Transfer Objects
│       ├── entity/      # Usuario, Ticket
│       ├── repository/  # JPA Repositories
│       ├── security/    # JWT, SecurityConfig, JwtAuthFilter
│       └── service/     # DashboardService + clients
├── ms-ventas/           # Microservicio de ventas
│   └── src/main/java/com/grupocordillera/ms_ventas/
│       ├── builder/     # VentaBuilder (Builder Pattern)
│       ├── controller/  # Venta, Producto, Sucursal, Reporte
│       ├── dto/         # DTOs
│       ├── entity/      # Venta, Producto, Sucursal, DetalleVenta
│       ├── event/       # VentaRegistradaEvent + Listeners (Observer)
│       ├── repository/  # JPA + Custom (SPs)
│       └── service/     # Lógica de negocio
├── ms-datos-org/        # Microservicio datos organizacionales
│   └── src/main/java/com/grupocordillera/datosorg/
│       ├── controller/  # Empleado, Departamento
│       ├── entity/      # Empleado, Departamento
│       ├── repository/  # JPA Repositories
│       ├── service/     # + EmpleadoFactory (Factory Method)
│       └── exception/   # GlobalExceptionHandler
├── ms-indicadores/      # Microservicio de indicadores KPI
│   └── src/main/java/com/grupocordillera/indicadores/
│       ├── controller/  # IndicadorController
│       ├── entity/      # Indicador, ValorIndicador, Categoria
│       ├── repository/  # JPA Repositories
│       ├── service/     # + CalculoIndicadorFactory
│       └── exception/   # GlobalExceptionHandler
├── archetypes/          # Arquetipos Maven
│   ├── ms-service-archetype/  # Base para microservicios Spring Boot
│   └── bff-archetype/         # Base para módulos BFF
├── docs/                # Documentación
│   ├── analisis-patrones-arquetipos.md
│   └── plan-branching.md
└── frontend/            # React 19 + Vite 8 + Tailwind CSS v4
    └── src/
        ├── api/         # Axios client + interceptors
        ├── components/  # Sidebar, Layout, ProtectedRoute, DetailModal
        ├── context/     # AuthContext (JWT management - Provider Pattern)
        ├── hooks/       # useApi, useMutation (Custom Hook Pattern)
        └── pages/       # Dashboard, Ventas, Productos, etc.
```

## 🔧 Decisiones Técnicas

1. **PostgreSQL único con schemas separados** — Se eligió una sola instancia de PostgreSQL con schemas (`ventas`, `datos_org`, `indicadores`) en lugar de bases de datos separadas por simplicidad operativa, manteniendo el aislamiento lógico.

2. **JWT con H2 en BFF** — La autenticación se centralizó en el BFF usando H2 en memoria para facilitar el desarrollo y pruebas, sin necesidad de una base de datos externa para usuarios.

3. **Factory Method para KPIs** — Permite agregar nuevas estrategias de cálculo sin modificar el código existente (Open/Closed Principle).

4. **Circuit Breaker en Gateway + BFF** — Protege contra fallos en cascada cuando un microservicio falla.

5. **Schema.sql ejecutado antes de Hibernate DDL** — Garantiza que los esquemas existan antes de que Hibernate intente crear tablas.

6. **Vite proxy en desarrollo** — Evita configurar CORS en los servicios backend durante desarrollo.
