# INFORME TECNICO FINAL
## ARQUITECTURA DE MICROSERVICIOS SPRING BOOT
### CASO: GRUPO CORDILLERA — PLATAFORMA DE MONITOREO INTELIGENTE

---

**Asignatura:** Desarrollo Fullstack III  
**Institucion:** Duoc UC  
**Carrera:** Ingenieria en Informatica  
**Integrantes:** Cristian Cerda, Gonzalo Berrios, Jaime Manzo  
**Fecha:** 2026  
**Version del sistema:** 1.0.1  
**Repositorio:** https://github.com/Basty66/cordillera

---

## INDICE

1. [Introduccion](#1-introduccion)
2. [Contexto del Negocio](#2-contexto-del-negocio)
3. [Problematica](#3-problematica)
4. [Arquitectura del Sistema](#4-arquitectura-del-sistema)
5. [Componentes del Sistema](#5-componentes-del-sistema)
6. [Patrones de Diseno Implementados](#6-patrones-de-diseno-implementados)
7. [Optimizacion de Rendimiento](#7-optimizacion-de-rendimiento)
8. [Estrategia de Branching y Control de Versiones](#8-estrategia-de-branching-y-control-de-versiones)
9. [Historial de Commits](#9-historial-de-commits)
10. [Despliegue y Contenedores](#10-despliegue-y-contenedores)
11. [Estrategia de Pruebas](#11-estrategia-de-pruebas)
12. [Integracion Continua](#12-integracion-continua)
13. [Justificacion Tecnica](#13-justificacion-tecnica)
14. [Limites Tecnicos](#14-limites-tecnicos)
15. [Limites Eticos](#15-limites-eticos)
16. [Beneficios](#16-beneficios)
17. [Conclusion](#17-conclusion)
18. [Referencias (Formato APA)](#18-referencias-formato-apa)

---

## 1. INTRODUCCION

El presente informe describe la implementacion de una plataforma de monitoreo inteligente para **Grupo Cordillera**, empresa del rubro retail y comercializacion con multiples sucursales a nivel nacional. La solucion consiste en una arquitectura de microservicios basada en **Spring Boot 3.2.5 (Java 21)** para el backend y **React 19 + Vite 8** para el frontend, con base de datos **PostgreSQL** alojada en **Neon.tech**.

La plataforma consolida informacion proveniente de diversos sistemas (punto de venta, e-commerce, inventarios, gestion financiera, atencion al cliente) en una solucion unificada basada en 5 microservicios independientes que se comunican a traves de un **API Gateway** con **Circuit Breaker** para tolerancia a fallos.

El sistema se encuentra desplegado localmente mediante **Docker Compose** con 6 contenedores, y cuenta con un pipeline de **Integracion Continua (CI)** via **GitHub Actions** que compila, prueba y construye imagenes Docker automaticamente.

A diferencia de la propuesta inicial que consideraba AWS y Django, la implementacion final opto por Spring Boot y ejecucion local con contenedores Docker, priorizando la modularidad, el rendimiento y la facilidad de desarrollo sin dependencia de servicios cloud de pago.

---

## 2. CONTEXTO DEL NEGOCIO

Grupo Cordillera es una empresa del rubro retail que opera con multiples sistemas tecnologicos incluyendo plataformas de ventas, inventario, comercio electronico y gestion financiera. Estos sistemas funcionan de manera independiente sin una integracion eficiente entre ellos, generando silos de informacion que dificultan el analisis y uso estrategico de los datos.

La organizacion requiere una plataforma unificada que permita:

- Visualizar en tiempo real los indicadores clave de rendimiento (KPIs) del negocio
- Gestionar el historico de ventas de todas las sucursales
- Acceder a datos organizacionales (empleados, departamentos)
- Generar reportes y dashboard ejecutivo con informacion consolidada
- Escalar el sistema a medida que crece la operacion

El sistema resuelve estas necesidades mediante una arquitectura moderna basada en microservicios, escalable y accesible via navegador web, sentando las bases para una transformacion digital orientada a datos.

---

## 3. PROBLEMATICA

La organizacion enfrenta las siguientes problematicas derivadas de su estructura tecnologica actual:

- **Existencia de procesos manuales** para consolidar informacion proveniente de distintos sistemas
- **Alta probabilidad de errores humanos** en la generacion de reportes
- **Falta de acceso a datos en tiempo real** sobre ventas, inventario y desempeno
- **Dificultad para escalar** los sistemas actuales ante el crecimiento del negocio
- **Baja integracion entre plataformas** tecnologicas, generando silos de informacion
- **Ausencia de una plataforma unificada** que centralice la informacion del negocio
- **Rendimiento insuficiente** en consultas a base de datos remota (Neon.tech) con alta latencia en consultas frias (>10 segundos)

Estas limitaciones afectan directamente la eficiencia operativa y generan riesgos en la toma de decisiones, ya que muchas veces se basa en informacion desactualizada o incompleta.

---

## 4. ARQUITECTURA DEL SISTEMA

### 4.1 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (React 19)                       │
│                  Vite 8 + Tailwind CSS v4                    │
│                  localhost:5173                              │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP / API REST
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (Spring Cloud Gateway)         │
│                    Puerto: 8084                               │
│              Circuit Breaker + TimeLimiter                    │
│              CORS abierto (CorsWebFilter)                     │
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

### 4.2 Tabla de Microservicios

| Servicio | Puerto | Base de Datos | Schema | Tecnologia | Responsabilidad |
|----------|--------|---------------|--------|------------|-----------------|
| **ms-ventas** | 8081 | PostgreSQL (Neon.tech) | `ventas` | Spring Boot 3.2.5 + JPA | Gestion de ventas, productos, sucursales |
| **ms-datos-org** | 8082 | PostgreSQL (Neon.tech) | `datos_org` | Spring Boot 3.2.5 + JPA | Empleados, departamentos, estructura organizacional |
| **ms-indicadores** | 8083 | PostgreSQL (Neon.tech) | `indicadores` | Spring Boot 3.2.5 + JPA | KPIs, categorias, valores de indicadores |
| **bff** | 8090 | H2 (memoria) | — | Spring Boot 3.2.5 + Security + JWT | BFF + Auth + Tickets + Reportes |
| **api-gateway** | 8084 | — | — | Spring Cloud Gateway 2023.0.3 | Enrutamiento, Circuit Breaker, CORS |

### 4.3 Stack Tecnologico

**Backend:**
- Java 21 + Spring Boot 3.2.5
- Spring Data JPA + Hibernate
- PostgreSQL 14 (Neon.tech) con esquemas separados
- Spring Cloud Gateway 2023.0.3
- Spring Security + JWT (jjwt 0.12.6)
- Resilience4j para Circuit Breaker
- Apache HttpComponents HttpClient 5 (PoolingHttpClientConnectionManager)
- Spring Cache (ConcurrentMapCacheManager)
- Swagger/OpenAPI (springdoc-openapi 2.5.0)
- Lombok 1.18.46
- H2 (base de datos en memoria para BFF)
- JaCoCo 0.8.12 (cobertura de pruebas)
- Maven Wrapper (mvnw)

**Frontend:**
- React 19.2.5
- Vite 8.0.10
- Tailwind CSS v4.2.4
- Axios 1.16.0
- React Router DOM 7.15.0
- Framer Motion 12.38.0
- Recharts 3.8.1 + Chart.js 4.5.1
- Lucide React 1.14.0

**Infraestructura:**
- Docker + Docker Compose
- GitHub Actions (CI)
- JaCoCo (cobertura de codigo 60%+)

---

## 5. COMPONENTES DEL SISTEMA

### 5.1 Frontend (React 19 + Vite 8)

La interfaz de usuario esta desarrollada con React 19 utilizando Vite 8 como bundler y Tailwind CSS v4 para estilos. Incluye:

- **Pagina de Login** con autenticacion JWT
- **Dashboard** con graficos interactivos (Recharts) que muestra:
  - Total de ventas, productos, sucursales, empleados
  - Ventas mensuales (grafico de barras)
  - Ventas por categoria (grafico circular)
  - Top 5 productos mas vendidos
- **Gestion de Ventas** con tabla paginada (20 registros por pagina), busqueda y exportacion CSV
- **Gestion de Productos** con CRUD completo, filtros por categoria e imagenes
- **Gestion de Tickets** con creacion, edicion y cambio de estado
- **Barra de estado** que muestra el health check de cada microservicio
- **Modo oscuro** con soporte de Tailwind CSS
- **Custom Hooks** (`useApi`, `useMutation`) que abstraen el data fetching con Axios
- **Context API** (`AuthContext`) para gestion de JWT

### 5.2 API Gateway (Spring Cloud Gateway)

Punto de entrada unico que enruta las solicitudes del frontend a los microservicios correspondientes:

- `/api/ventas/**` → ms-ventas (8081) con Circuit Breaker
- `/api/productos/**` → ms-ventas (8081) con Circuit Breaker
- `/api/sucursales/**` → ms-ventas (8081) con Circuit Breaker
- `/api/empleados/**` → ms-datos-org (8082) con Circuit Breaker
- `/api/departamentos/**` → ms-datos-org (8082) con Circuit Breaker
- `/api/indicadores/**` → ms-indicadores (8083) con Circuit Breaker
- `/api/auth/**` → BFF (8090) sin Circuit Breaker
- `/api/tickets/**` → BFF (8090) sin Circuit Breaker
- `/api/reportes/**` → BFF (8090) sin Circuit Breaker
- `/api/bff/**` → BFF (8090) con Circuit Breaker
- `/health` → Health check interno
- Configuracion CORS abierta via `CorsWebFilter`

### 5.3 BFF — Backend For Frontend (Spring Boot)

Microservicio que actua como intermediario entre el frontend y los microservicios internos. Implementa:

- **Autenticacion JWT**: login, registro, gestion de usuarios (base H2 en memoria)
- **Dashboard consolidado**: `DashboardService.obtenerDashboard()` que agrega datos de los 3 microservicios internos en paralelo
- **Gestion de Tickets**: CRUD completo con estados (ABIERTO, EN_PROGRESO, RESUELTO, CERRADO)
- **Reportes**: estadisticas del sistema y exportacion
- **Cache distribuida**: `@Cacheable("dashboard")` con `ConcurrentMapCacheManager`
- **Pool de conexiones HTTP**: `PoolingHttpClientConnectionManager` (50 conexiones totales, 10 por ruta)
- **Paralelizacion asincrona**: `ThreadPoolTaskExecutor` con `CompletableFuture.supplyAsync()`

### 5.4 ms-ventas (Spring Boot + JPA)

Microservicio principal de gestion de ventas:

- **Entidades**: `Venta`, `DetalleVenta`, `Producto`, `Sucursal`
- **Ventas paginadas**: endpoint `/api/ventas` con parametros `pagina` y `tamano`, retorna `Page<Venta>` de Spring Data
- **Cache en memoria**: `@Cacheable("ventas")` en consultas paginadas, `@CacheEvict` en registro de ventas
- **Cache en productos**: `@Cacheable("productos")` con `@CacheEvict` en operaciones CRUD
- **Cache en sucursales**: `@Cacheable("sucursales")` con conteo cacheado
- **Indices de base de datos**: covering indexes para consultas de reportes por fecha, sucursal y producto
- **Observer Pattern**: `VentaRegistradaEvent` + `EventoListener` para notificaciones
- **Builder Pattern**: `VentaBuilder` para construccion paso a paso de objetos Venta
- **OpenAPI/Swagger**: documentacion interactiva de la API

### 5.5 ms-datos-org (Spring Boot + JPA)

Microservicio de datos organizacionales:

- **Entidades**: `Empleado`, `Departamento`
- **Cache**: `@Cacheable("empleados")`, `@Cacheable("departamentos")` con `@CacheEvict` en escritura
- **Factory Method Pattern**: `EmpleadoFactory` para creacion de empleados segun tipo
- **N+1 Query resuelto**: `@EntityGraph(attributePaths = {"departamento"})` en `findAll()` de EmpleadoRepository
- **Endpoint de conteo**: `/api/empleados/count` para dashboard

### 5.6 ms-indicadores (Spring Boot + JPA)

Microservicio de indicadores KPI:

- **Entidades**: `Indicador`, `ValorIndicador`, `Categoria`
- **Cache**: `@Cacheable("indicadores")`, `@Cacheable("valores")`, `@Cacheable("categorias")` con `@CacheEvict`
- **Strategy Pattern**: `CalculoStrategy` con 3 implementaciones (VENTAS, INVENTARIO, RENTABILIDAD)
- **Factory Method Pattern**: `CalculoIndicadorFactory` para crear la estrategia segun tipo
- **N+1 Query resuelto**: `JOIN FETCH` en consultas de Indicador y ValorIndicador

---

## 6. PATRONES DE DISENO IMPLEMENTADOS

| Patron | Ubicacion | Proposito |
|--------|-----------|-----------|
| **Repository** | Todos los modulos (JPA Repository) | Abstraccion de persistencia de datos |
| **Factory Method** | `ms-datos-org`: `EmpleadoFactory` | Creacion de empleados segun tipo |
| **Factory Method** | `ms-indicadores`: `CalculoIndicadorFactory` | Calculo de KPIs segun estrategia |
| **Strategy** | `ms-indicadores`: `CalculoStrategy` + 3 impls | Algoritmos intercambiables para KPIs |
| **Observer** | `ms-ventas`: `VentaRegistradaEvent` + Listeners | Notificacion de eventos de venta |
| **Builder** | `ms-ventas`: `VentaBuilder` | Construccion paso a paso de objetos Venta |
| **Circuit Breaker** | `api-gateway` + `bff` (Resilience4j) | Tolerancia a fallos en comunicacion |
| **BFF (Backend For Frontend)** | `bff/` | Agregacion de datos para el frontend |
| **API Gateway** | `api-gateway/` | Punto unico de entrada, enrutamiento |
| **Custom Hook** | `frontend/src/hooks/useApi.js` | Abstraccion de data fetching en React |
| **Provider** | `frontend/src/context/AuthContext.jsx` | Gestion de estado de autenticacion |
| **Cache-Aside** | Todos los servicios con `@Cacheable` | Cache en memoria para reducir latencia |

---

## 7. OPTIMIZACION DE RENDIMIENTO

Se implementaron multiples optimizaciones para reducir la latencia, especialmente considerando que la base de datos PostgreSQL remota (Neon.tech) tiene alta latencia en consultas iniciales (~10 segundos en consultas frias).

### 7.1 Cache en Memoria

Se agrego `spring-boot-starter-cache` con `@EnableCaching` y `ConcurrentMapCacheManager` en todos los microservicios:

- **ms-ventas**: `@Cacheable("ventas")`, `@Cacheable("productos")`, `@Cacheable("sucursales")` con `@CacheEvict` en operaciones de escritura
- **ms-datos-org**: `@Cacheable("empleados")`, `@Cacheable("departamentos")` con `@Transactional(readOnly = true)`
- **ms-indicadores**: `@Cacheable("indicadores")`, `@Cacheable("valores")`, `@Cacheable("categorias")`
- **bff**: `@Cacheable("dashboard")`

**Resultados con cache caliente:**
- Ventas: 0.066s
- Productos: 0.031s
- Sucursales: 0.026s
- Empleados: 0.045s
- Departamentos: 0.017s
- Indicadores: 0.018s
- Valores actuales: 0.015s
- Dashboard: 0.018s

### 7.2 Eliminacion de N+1 Queries

Se corrigieron todos los problemas N+1 en las relaciones JPA:

- **VentaRepository**: `@EntityGraph(attributePaths = {"sucursal", "detalles", "detalles.producto"})` en `findAll()`
- **VentaRepository (paginado)**: `LEFT JOIN FETCH v.sucursal LEFT JOIN FETCH v.detalles d LEFT JOIN FETCH d.producto` con `countQuery` separado
- **EmpleadoRepository**: `@EntityGraph(attributePaths = {"departamento"})` en `findAll()`
- **IndicadorRepository**: `findAllWithCategoria()` con `JOIN FETCH i.categoria`
- **ValorIndicadorRepository**: `findAllWithRelations()` con doble `JOIN FETCH`

### 7.3 Paginacion de Ventas

Se agrego paginacion con `Page<Venta>` (20 registros por pagina) para evitar la carga completa de ~1772 registros en el frontend:

- Endpoint: `GET /api/ventas?pagina=0&tamano=20`
- Custom JPQL con `LEFT JOIN FETCH` + `countQuery` separado
- Cache por clave `pagina + '-' + tamano`

### 7.4 Paralelizacion en BFF

`DashboardService.obtenerDashboard()` realiza 7 llamadas a microservicios internos en paralelo mediante `CompletableFuture.supplyAsync()` con `ThreadPoolTaskExecutor` configurado:

- `corePoolSize = 5`, `maxPoolSize = 10`, `queueCapacity = 25`

### 7.5 Pool de Conexiones HTTP

`RestTemplateConfig` en BFF configurado con `PoolingHttpClientConnectionManager`:

- Maximo total: 50 conexiones
- Maximo por ruta: 10 conexiones
- Timeout de conexion: 5 segundos
- Timeout de lectura: 10 segundos

### 7.6 Indices de Base de Datos

Covering indexes agregados en `schema.sql` de ms-ventas:

```sql
CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas.transacciones_venta (fecha_venta);
CREATE INDEX IF NOT EXISTS idx_ventas_sucursal ON ventas.transacciones_venta (sucursal_id);
CREATE INDEX IF NOT EXISTS idx_ventas_fecha_monto ON ventas.transacciones_venta (fecha_venta, monto_total);
CREATE INDEX IF NOT EXISTS idx_detalle_ventas_producto ON ventas.detalle_ventas (producto_id);
CREATE INDEX IF NOT EXISTS idx_detalle_ventas_venta ON ventas.detalle_ventas (venta_id);
```

Adicionalmente, anotaciones `@Table(indexes = ...)` en las entidades `Venta` y `DetalleVenta`.

### 7.7 Open Session In View

Se agrego `spring.jpa.open-in-view=true` en ms-ventas para mantener la sesion de Hibernate abierta durante la serializacion JSON, evitando el error `failed to lazily initialize a collection - no Session`.

---

## 8. ESTRATEGIA DE BRANCHING Y CONTROL DE VERSIONES

### 8.1 Modelo de Branching

Se utiliza un modelo **Git Flow Adaptado** con las siguientes ramas:

| Rama | Proposito |
|------|-----------|
| `master` | Rama principal de produccion |
| `develop` | Rama de integracion de caracteristicas |
| `feature/patrones-diseno` | Implementacion de patrones de diseno |
| `feature/arquetipos-maven` | Creacion de arquetipos Maven |
| `feature/frontend-components` | Componentes y hooks de React |
| `feature/documentacion` | Documentacion del proyecto |
| `feature/pruebas-unitarias` | Pruebas unitarias con JaCoCo |
| `release/1.0.0` | Preparacion de release v1.0.0 |
| `rama-gonzalo` | Rama de trabajo paralelo de Gonzalo |

### 8.2 Flujo de Trabajo

1. Se crean ramas `feature/*` a partir de `develop`
2. Cada feature se integra a `develop` via merge
3. `develop` se integra a `master` via release branches
4. `rama-gonzalo` trabaja en paralelo y se mergea a `master` periodicamente
5. Los conflictos se resuelven documentando la decision tomada

### 8.3 Convencion de Commits

Se utiliza **Conventional Commits** con los siguientes tipos:

- `feat:` — Nueva caracteristica
- `fix:` — Correccion de errores
- `docs:` — Documentacion
- `test:` — Pruebas
- `refactor:` — Refactorizacion
- `merge:` — Merges de ramas

### 8.4 Versionamiento Semantico

- **v1.0.0** — Primera version estable con todos los patrones, arquetipos y funcionalidad basica
- **v1.0.1** — Entrega final con cobertura 60%+, pruebas unitarias, stored procedures, modulo npm

### 8.5 Conflictos Resueltos

Se documentaron 3 conflictos principales durante el merge entre `rama-gonzalo` y `master`:

1. **pom.xml**: Conflictos en dependencias (Lombok, connection-pool, cache)
2. **application.properties**: Puerto del servicio y configuraciones de base de datos
3. **VentaService.java**: Implementacion de builder y manejo de detalles duplicados

---

## 9. HISTORIAL DE COMMITS

### Rama `master` (linea principal)

| Commit | Fecha | Mensaje |
|--------|-------|---------|
| `2b23af0` | 2026-05-14 | fix: corregir error ventas.filter is not a function y lazy init en serializacion JSON |
| `4c4bb43` | 2026-05-14 | fix: migrar imagenes locales para productos 21-30 en base de datos existente |
| `d2e2f9e` | 2026-05-14 | Agrega imagenes reales para productos de Ropa (IDs 21-30) |
| `ffab706` | 2026-05-14 | fix: eliminar N+1 en empleados e indicadores, agregar cache a departamentos y reiniciar servicios |
| `c0cfd2d` | 2026-05-14 | feat: agregar cache en memoria a ms-datos-org, ms-indicadores y servicios restantes de ms-ventas |
| `2e1858b` | 2026-05-14 | fix: optimizar consulta paginada de ventas con JPQL directo y cache en memoria |
| `af0a76a` | 2026-05-14 | Fix: acento en categoria Electronica para filtros frontend |
| `93f4c04` | 2026-05-14 | Fix: filtros de productos funcionales con campo categoria |
| `06b86f0` | 2026-05-14 | Fix: proxy Vite y CORS para evitar error de conexion entre frontend y gateway |
| `84e6512` | 2026-05-14 | feat: optimizar rendimiento con cache, paralelizacion, pool de conexiones, paginacion e indices |
| `651067a` | 2026-05-14 | fix: corregir test ProductoServiceTest (encoding caracteres acentuados) |
| `f85c574` | 2026-05-14 | fix: agregar permiso ejecutable a scripts mvnw |
| `59a2dc8` | 2026-05-14 | feat: Docker, CSV export, CRUD productos y mejoras frontend/backend |
| `16ba46d` | 2026-05-14 | Agregar imagenes reales para productos de electronica (IDs 1-10) |
| `e035486` | 2026-05-14 | docs: agregar conflictos resueltos y actualizar documentacion de branching (tag v1.0.1) |
| `678931e` | 2026-05-14 | feat: completar entregable con arquetipos, tests, SP, modulo npm y documentacion |
| `dc926d1` | 2026-05-14 | feat: agregar cobertura de pruebas con JaCoCo al 60%+ en todos los modulos |
| `38e67ef` | 2026-05-14 | feat: agregar data warehouse, datos semilla, barra de estado, dashboard y reportes |
| `d9b2d82` | 2026-05-14 | feat: agregar documentacion Swagger/OpenAPI y anotaciones a todos los controladores |
| `86fb465` | 2026-05-14 | docs: agregar documentacion PDF de patrones y plan de branching |
| `730b367` | 2026-05-14 | release: v1.0.0 - Plataforma de Monitoreo Grupo Cordillera (tag v1.0.0) |

### Rama `rama-gonzalo` (trabajo paralelo)

| Commit | Mensaje |
|--------|---------|
| `bc3a6fc` | Fix: configuracion Lombok compatible con Java 25 e imagenes de hogar |
| `fed4797` | Agregar imagenes reales para productos de hogar (IDs 11-20) |
| `8b92c72` | Agregar guia de instalacion con dependencias y librerias del sistema |

### Ramas `feature/*` (ramas de desarrollo)

| Rama | Commits Clave |
|------|---------------|
| `feature/patrones-diseno` | Implementacion de patrones Observer, Builder, Factory, Strategy |
| `feature/arquetipos-maven` | Creacion de arquetipos ms-service-archetype y bff-archetype |
| `feature/frontend-components` | Custom hooks useApi, useMutation |
| `feature/documentacion` | READMEs, repositorios.txt, documentacion de patrones |
| `feature/pruebas-unitarias` | Tests para Builder, Observer, servicios y estrategia |

---

## 10. DESPLIEGUE Y CONTENEDORES

### 10.1 Docker Compose

El sistema se despliega con 6 servicios definidos en `docker-compose.yml`:

```yaml
services:
  ms-ventas:       puerto 8081, schema ventas
  ms-datos-org:    puerto 8082, schema datos_org
  ms-indicadores:  puerto 8083, schema indicadores
  bff:             puerto 8090, depende de los 3 microservicios
  api-gateway:     puerto 8084, depende de todos los anteriores
  frontend:        puerto 5173:80, depende de api-gateway
```

Todos los servicios tienen `restart: unless-stopped` y se pasan las variables de entorno de Neon.tech.

### 10.2 Dockerfiles

Cada servicio tiene un Dockerfile multi-stage:

1. **Build stage**: `eclipse-temurin:21-jdk-alpine` — compila con `mvnw clean package -DskipTests`
2. **Runtime stage**: `eclipse-temurin:21-jre-alpine` — ejecuta `java -jar app.jar`

El frontend usa `node:20-alpine` para build y `nginx:alpine` para produccion.

### 10.3 Variables de Entorno

Definidas en `.env.example`:
```
DB_HOST=ep-your-project.us-east-2.aws.neon.tech
DB_PORT=5432
DB_NAME=neondb
DB_USER=your_username
DB_PASSWORD=your_password
```

---

## 11. ESTRATEGIA DE PRUEBAS

### 11.1 Pruebas Unitarias

- **Framework**: JUnit 5 + Mockito
- **Cobertura minima**: 60% (validado con JaCoCo)
- **Modulos probados**:
  - `ms-ventas`: Builder Pattern, Observer/Event listeners, servicios de ventas
  - `ms-datos-org`: EmpleadoService, DepartamentoService, EmpleadoFactory
  - `ms-indicadores`: CalculoStrategy, CalculoIndicadorFactory, IndicadorService
  - `bff`: DashboardService, AuthController
  - `api-gateway`: RouteConfig, FallbackController

### 11.2 Configuracion JaCoCo

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <configuration>
    <rules>
      <rule><element>PACKAGE</element><limits><limit><counter>LINE</counter><value>COVEREDRATIO</value><minimum>0.60</minimum></limit></limits></rule>
    </rules>
  </configuration>
</plugin>
```

### 11.3 Stored Procedures

Se implemento un stored procedure en PostgreSQL para calculo de ventas en periodo:

```sql
CREATE FUNCTION ventas.calcular_ventas_periodo(fecha_inicio TEXT, fecha_fin TEXT)
RETURNS NUMERIC AS $$
  SELECT COALESCE(SUM(v.monto_total), 0)
  FROM ventas.transacciones_venta v
  WHERE v.fecha_venta >= fecha_inicio::DATE
    AND v.fecha_venta <= fecha_fin::DATE;
$$ LANGUAGE plpgsql;
```

---

## 12. INTEGRACION CONTINUA

Se definio un pipeline CI en `.github/workflows/ci.yml` con 3 jobs:

### Job 1: build-backend
- Matrix con 5 modulos (api-gateway, bff, ms-ventas, ms-datos-org, ms-indicadores)
- JDK 21 en ubuntu-latest
- Ejecuta `./mvnw clean test -q`

### Job 2: build-frontend
- Node.js 20
- Ejecuta `npm ci` y `npm run build`

### Job 3: docker-images
- Depende de los jobs 1 y 2
- Construye las 6 imagenes Docker usando `docker/build-push-action`

---

## 13. JUSTIFICACION TECNICA

### 13.1 Spring Boot vs Django

La implementacion final opto por **Spring Boot 3.2.5 (Java 21)** en lugar de Django por las siguientes razones:

- **Tipado fuerte** que reduce errores en tiempo de compilacion
- **Ecosistema Spring** maduro para microservicios (Cloud Gateway, Circuit Breaker, Data JPA)
- **Rendimiento superior** en operaciones de I/O y concurrencia
- **Facilidad de integracion** con JPA/Hibernate para PostgreSQL
- **Arquetipos Maven** que permiten generar nuevos microservicios rapidamente

### 13.2 PostgreSQL con Schemas Separados

Se eligio una sola instancia de PostgreSQL con 3 schemas (`ventas`, `datos_org`, `indicadores`) en lugar de bases de datos separadas, manteniendo el aislamiento logico pero simplificando la operacion y reduciendo costos de conexion.

### 13.3 Cache en Memoria vs Redis

Se opto por `ConcurrentMapCacheManager` (cache en memoria) en lugar de Redis por:

- Simplicidad operativa (sin infraestructura adicional)
- Suficiente para el volumen de datos actual (~1772 ventas, 60 productos, 35 empleados)
- Latencia minima en cache caliente (<70ms en todos los endpoints)

### 13.4 Patrones de Diseno

- **Microservicios**: desacoplan funcionalidades, facilitando mantenimiento y escalabilidad
- **API Gateway**: centraliza el acceso, mejora seguridad y control
- **Circuit Breaker**: protege contra fallos en cascada
- **BFF**: optimiza la comunicacion frontend-backend reduciendo llamadas
- **Factory/Strategy**: permiten agregar nuevas implementaciones sin modificar codigo existente (Open/Closed Principle)

---

## 14. LIMITES TECNICOS

- **Latencia en base de datos remota**: Las consultas iniciales a Neon.tech pueden demorar ~10 segundos (resuelto con cache en memoria)
- **Cache en memoria volatil**: Al reiniciar los servicios, el cache se pierde y las primeras consultas son lentas
- **Dependencia de Docker**: El despliegue completo requiere Docker Desktop corriendo
- **Sin base de datos compartida**: Cada microservicio tiene su propio schema, requiriendo coordinacion para consultas cross-schema
- **Sin replicacion ni alta disponibilidad**: La instancia actual de Neon.tech es un solo nodo
- **Escalabilidad horizontal limitada**: La cache en memoria no se comparte entre instancias del mismo servicio
- **Sin autenticacion por microservicio**: La autenticacion centralizada en el BFF simplifica pero crea un punto unico de fallo

---

## 15. LIMITES ETICOS

La implementacion de esta solucion considera aspectos eticos relevantes en el manejo de datos:

- **Proteccion de informacion sensible**: Los datos de usuarios y empleados se almacenan con JWT para autenticacion
- **Seguridad en el acceso**: Las rutas del API Gateway requieren token JWT valido
- **Transparencia en el uso de los datos**: La API esta documentada con Swagger/OpenAPI
- **Uso responsable de los indicadores**: Los KPIs se calculan con datos reales del sistema
- **Credenciales de prueba documentadas**: Se proporcionan usuarios de prueba con roles especificos

---

## 16. BENEFICIOS

- **Visualizacion en tiempo real** de ventas, indicadores y datos organizacionales
- **Acceso inmediato a KPIs** del negocio mediante dashboard consolidado
- **Centralizacion de informacion** en una sola plataforma con 5 microservicios integrados
- **Documentacion interactiva** de la API via Swagger/OpenAPI
- **Interfaz responsive** accesible desde cualquier dispositivo con modo oscuro
- **Reduccion de procesos manuales** al automatizar la consolidacion de datos
- **Toma de decisiones basada en datos actualizados** con cache caliente (<70ms de respuesta)
- **Escalabilidad** para incorporar nuevos modulos mediante arquetipos Maven
- **Tolerancia a fallos** con Circuit Breaker en todas las rutas criticas
- **Rendimiento optimizado** con paginacion, cache, paralelizacion e indices de base de datos
- **Despliegue automatizado** via Docker Compose y CI/CD con GitHub Actions
- **Calidad de codigo garantizada** con cobertura de pruebas del 60%+ via JaCoCo
- **Versionamiento semantico** con v1.0.0 y v1.0.1 documentados

---

## 17. CONCLUSION

La plataforma de monitoreo inteligente para Grupo Cordillera representa una solucion moderna y efectiva para los problemas de fragmentacion de datos y falta de integracion entre sistemas.

La arquitectura basada en microservicios con Spring Boot, React y PostgreSQL permite centralizar la informacion, automatizar procesos y generar valor a partir de los datos, facilitando la toma de decisiones estrategicas.

Las optimizaciones de rendimiento implementadas (cache, paralelizacion, paginacion, indices, eliminacion de N+1) redujeron los tiempos de respuesta de ~10 segundos (consulta fria) a menos de 70ms (cache caliente), demostrando la efectividad de las tecnicas aplicadas.

La estrategia de branching Git Flow Adaptado permitio el trabajo paralelo de multiples desarrolladores, con 9 ramas activas y mas de 40 commits documentados. Los conflictos se resolvieron y documentaron adecuadamente.

El pipeline de CI/CD con GitHub Actions y JaCoCo garantiza la calidad del codigo con cobertura minima del 60%, mientras que Docker Compose facilita el despliegue reproducible en cualquier entorno.

La plataforma posiciona a Grupo Cordillera como una organizacion orientada a datos, preparada para enfrentar desafios futuros y adaptarse a un entorno tecnologico en constante evolucion.

---

## 18. REFERENCIAS (FORMATO APA)

1. Newman, S. (2015). *Building Microservices*. O'Reilly Media.
2. Richardson, C. (2018). *Microservices Patterns*. Manning.
3. Spring Documentation. (2026). *Spring Boot 3.2.5*. https://spring.io/projects/spring-boot
4. Spring Cloud Gateway. (2026). *Spring Cloud Gateway*. https://spring.io/projects/spring-cloud-gateway
5. React Documentation. (2026). *React 19*. https://react.dev/
6. Vite Documentation. (2026). *Vite 8*. https://vite.dev/
7. Tailwind CSS Documentation. (2026). *Tailwind CSS v4*. https://tailwindcss.com/
8. Neon Documentation. (2026). *Neon Serverless PostgreSQL*. https://neon.tech/docs
9. Docker Documentation. (2026). *Docker Compose*. https://docs.docker.com/compose/
10. Resilience4j Documentation. (2026). *Resilience4j Circuit Breaker*. https://resilience4j.readme.io/
11. PostgreSQL Documentation. (2026). *PostgreSQL 14*. https://www.postgresql.org/docs/14/
12. Hibernate Documentation. (2026). *Hibernate ORM*. https://hibernate.org/orm/
13. JaCoCo Documentation. (2026). *JaCoCo Java Code Coverage*. https://www.eclemma.org/jacoco/
14. GitHub Actions Documentation. (2026). *GitHub Actions*. https://docs.github.com/en/actions
15. Axios Documentation. (2026). *Axios HTTP Client*. https://axios-http.com/
16. Framer Motion Documentation. (2026). *Framer Motion*. https://www.framer.com/motion/
17. Recharts Documentation. (2026). *Recharts*. https://recharts.org/
18. Amazon Web Services. (2024). *AWS Architecture Best Practices*. https://aws.amazon.com/architecture
