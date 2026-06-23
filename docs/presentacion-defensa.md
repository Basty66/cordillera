# Presentación Defensa Oral — Grupo Cordillera
## DSY1106 — Desarrollo Fullstack III | Parcial N°3

---

## Slide 1: Portada (30s)

**Proyecto:** Sistema de Gestión de Ventas Multisucursal  
**Arquitectura:** Microservicios + BFF + API Gateway  
**Stack:** React 19 + Spring Boot 3.2 + Java 21 + PostgreSQL (Neon)  
**Repositorio:** `github.com/Basty66/cordillera`  
**Equipo:** Grupo Cordillera

---

## Slide 2: Problemática y Técnicas de Ideación (1 min) → **Indicador 5**

### Problemática del Cliente
Cadena de retail chilena con múltiples sucursales necesita:
- **Unificar** ventas, datos organizacionales e indicadores de gestión
- **Escalar** componentes de forma independiente según demanda
- **Separar** responsabilidades por dominio de negocio
- **Integrar** APIs externas (clima, indicadores económicos)
- **Asegurar** calidad mediante pruebas automatizadas

### Técnicas de Ideación Aplicadas
| Técnica | Aplicación |
|---------|-----------|
| **Domain-Driven Design (DDD)** | Identificamos 3 dominios: Ventas, Datos Organizacionales, Indicadores |
| **Event Storming** | Mapeamos eventos de negocio: venta creada, stock actualizado, empleado registrado |
| **Descomposición por capacidades** | Cada microservicio encapsula una capacidad de negocio específica |

### Justificación de Microservicios Específicos
- **ms-ventas:** Corazón del negocio — catálogo, transacciones, sucursales, reportes, APIs externas
- **ms-datos-org:** Datos maestro — departamentos y empleados, alta cohesión
- **ms-indicadores:** KPI y métricas — lógica de cálculo aislada con Strategy Pattern
- **BFF:** Fachada para el frontend — orquesta, autentica con JWT, NO tiene lógica de negocio propia
- **API Gateway:** Punto único de entrada — routing, circuit breakers, seguridad perimetral

> No usamos monolito porque cada dominio tiene diferentes requisitos de escalabilidad, despliegue y tecnología.

---

## Slide 3: Arquitectura de Microservicios (1.5 min) → **Indicador 1**

### Diagrama de Arquitectura

```
                    ┌─────────────────────┐
                    │   Frontend React    │
                    │   localhost:5173    │
                    └──────────┬──────────┘
                               │ HTTP REST
                    ┌──────────▼──────────┐
                    │   API Gateway       │ ← Punto único de entrada
                    │   Spring Cloud GW   │ ← Circuit Breakers
                    │   Puerto 9084       │ ← Fallbacks
                    └──┬───┬───┬───┬──────┘
                       │   │   │   │
              ┌────────┘   │   │   └──────────┐
              ▼            ▼   ▼              ▼
       ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐
       │   BFF    │ │ ms-ventas│ │ms-datos- │ │ms-indicadores│
       │ :8090    │ │ :8081    │ │ org:8082 │ │ :8083        │
       │ Puerto   │ │ Puerto   │ │ Puerto   │ │ Puerto       │
       │ host:9080│ │ host:9081│ │ host:9082│ │ host:9083    │
       ├──────────┤ ├──────────┤ ├──────────┤ ├──────────────┤
       │ Auth JWT │ │ Ventas   │ │ Depts.   │ │ Strategy     │
       │ Tickets  │ │ Prod.    │ │ Empleados│ │ Factory      │
       │ Dashbrd  │ │ Sucurs.  │ │          │ │ Calculo KPI  │
       │ H2 mem   │ │ Reportes │ │          │ │              │
       └──────────┘ │ Clima    │ │          │ │              │
                    │ Econ.    │ │          │ │              │
                    └─────┬────┘ └─────┬────┘ └──────┬───────┘
                          │            │              │
                          └─────┬──────┴──────────────┘
                                │
                     ┌──────────▼──────────┐
                     │    Neon PostgreSQL  │
                     │    Cloud - AWS us   │
                     │    Conexión SSL     │
                     └─────────────────────┘
```

### Roles y Responsabilidades
| Componente | Responsabilidad | Justificación |
|-----------|----------------|---------------|
| **API Gateway** | Routing único, circuit breakers, fallbacks, salud | Evita exponer direcciones internas, tolerancia a fallos |
| **BFF** | Autenticación JWT, tickets soporte, dashboard orquestado | Fachada que abstrae la topología de micros al frontend |
| **ms-ventas** | CRUD ventas, productos, sucursales, reportes, APIs clima/económico | Dominio principal del negocio |
| **ms-datos-org** | CRUD departamentos, empleados con factory pattern | Datos maestro organizacionales |
| **ms-indicadores** | Cálculo de KPI con Strategy Pattern | Lógica aislada, fácil de extender |

### Patrones de Arquitectura Aplicados
1. **BFF (Backend For Frontend)** — Un backend específico para las necesidades del frontend
2. **API Gateway** — Punto único de entrada con routing y tolerancia a fallos
3. **Circuit Breaker** — resilience4j en todas las llamadas BFF → microservicios
4. **Database per Service** — Cada microservicio tiene su esquema de base de datos

---

## Slide 4: Frontend (1.5 min) → **Indicador 2**

### Framework Moderno: React 19 + Vite
```
frontend/
├── src/
│   ├── api/client.js        ← Axios, llama al gateway
│   ├── context/
│   │   ├── AuthContext.jsx   ← Estado de autenticación
│   │   └── ThemeContext.jsx  ← Tema claro/oscuro
│   ├── hooks/
│   │   ├── useApi.js        ← Hook genérico para llamadas API
│   │   └── usePagination.js ← Paginación reusable
│   ├── pages/               ← 10 páginas
│   ├── components/          ← 15 componentes reusables
│   └── __tests__/           ← 18 archivos de test
├── package.json
└── vite.config.js
```

### Tecnologías Aplicadas
| Tecnología | Versión | Propósito |
|-----------|:-------:|-----------|
| React | 19.2 | Framework UI |
| Vite | 4.x | Build tool |
| Tailwind CSS | 4.x | Estilos utilitarios |
| Framer Motion | 12.x | Animaciones fluidas |
| Chart.js / Recharts | — | Gráficos de reportes |
| React Router | 7.x | Navegación SPA |
| Lucide React | — | Iconos |

### Características Implementadas
- **Autenticación por rol** (ADMIN, VENDEDOR, BODEGUERO) con rutas protegidas
- **Dashboard** con indicadores, ventas mensuales, top productos
- **CRUD** de productos, ventas, tickets, sucursales, empleados
- **Mapa de sucursales** con clima en tiempo real
- **Tema claro/oscuro** persistente
- **Buscador global** con SearchBar
- **Modal de detalle** para productos y tickets
- **Splash screen** con animación canvas

### Testing Frontend
- **Framework:** Vitest + React Testing Library
- **Resultados:** 45 tests, 18 archivos, 0 fallos

---

## Slide 5: Backend — Componentes y Tecnologías (2 min) → **Indicador 2 + 6**

### Stack Tecnológico
| Componente | Lenguaje | Framework | Testing | Persistencia |
|-----------|:--------:|:---------:|:-------:|:------------:|
| API Gateway | Java 21 | Spring Cloud Gateway 4 | JUnit 5 | — |
| BFF | Java 21 | Spring Boot 3.2 + Security | JUnit 5 + Mockito | H2 |
| ms-ventas | Java 21 | Spring Boot 3.2 + JPA | JUnit 5 + Mockito | PostgreSQL |
| ms-datos-org | Java 21 | Spring Boot 3.2 + JPA | JUnit 5 + Mockito | PostgreSQL |
| ms-indicadores | Java 21 | Spring Boot 3.2 + JPA | JUnit 5 + Mockito | PostgreSQL |
| Frontend | JS/JSX | React 19 + Vite | Vitest + RTL | — |

### ¿Cómo se integran para cumplir requerimientos?
```
1. Frontend llama a API Gateway (única URL: localhost:9084)
2. Gateway enruta según el path:
   - /api/auth/**      → BFF
   - /api/ventas/**    → ms-ventas
   - /api/departamentos/** → ms-datos-org
   - /api/indicadores/**   → ms-indicadores
   - /api/tickets/**   → BFF
3. Cada microservicio consulta su esquema en Neon PostgreSQL
4. BFF orquesta datos de múltiples micros para el dashboard
5. Circuit Breaker en cada ruta: si un micro falla, responde fallback
```

### APIs Externas Integradas
| API | Propósito | Estado | Mecanismo de Fallback |
|-----|-----------|:------:|-----------------------|
| **mindicador.cl** | UF, USD, UTM, IPC, Euro | ✅ Real | Valores hardcodeados + caché 5 min |
| **OpenWeatherMap** | Clima 9 ciudades chilenas | ✅ Real | Datos simulados por ciudad + caché 10 min |

---

## Slide 6: Persistencia de Datos con JPA (1.5 min) → **Indicador 3**

### Estrategia de Persistencia
- **Base de datos:** Neon PostgreSQL cloud (plan gratuito, AWS us-east-1)
- **ORM:** JPA / Hibernate 6.4 con `ddl-auto: update`
- **Pool de conexiones:** HikariCP con SSL habilitado
- **BFF:** H2 en memoria con `ddl-auto: create-drop` para auth y tickets

### Esquemas por Microservicio

#### ms-ventas (`ventas.*`)
```
productos(id, nombre, descripcion, precio, stock, categoria, imagen_url)
ventas(id, total, sucursal_id, fecha, estado)
venta_productos(id, venta_id, producto_id, cantidad, precio_unitario)
sucursales(id, nombre, direccion, ciudad, imagen_url)
```

#### ms-datos-org (`datos_org.*`)
```
departamentos(id, nombre, descripcion)
empleados(id, nombre, cargo, salario, departamento_id)
```

#### ms-indicadores (`indicadores.*`)
```
categorias_indicador(id, nombre, descripcion)
valores_indicador(id, categoria_id, valor_actual, periodo, fecha_calculo)
```

### Data Initializer
Cada microservicio tiene un `DataInitializer` con `@PostConstruct` que:
1. Verifica si ya existen datos (`count() > 0` → salta)
2. Carga datos semilla: ~1800 productos, 1773 ventas, 35 empleados, 8 departamentos, 12 sucursales
3. Usa `@Transactional` para operaciones atómicas

### Comunicación entre servicios
- Frontend ↔ Gateway: HTTP REST (JSON)
- Gateway ↔ Microservicios: HTTP REST (rutas Docker internas)
- BFF ↔ Microservicios: RestTemplate con Circuit Breaker (resilience4j)
- **No hay comunicación directa entre microservicios** — toda pasa por el Gateway

---

## Slide 7: Pruebas Unitarias y Cobertura (2.5 min) → **Indicador 4 + 8**

### Estrategia de Testing

**Backend** — Triple capa de pruebas:
1. **Unitarias (Service):** Mockito aísla el servicio, prueba lógica de negocio
2. **Integración (Controller):** `@WebMvcTest` + MockMvc, prueba API REST
3. **Repositorio:** `@DataJpaTest`, prueba consultas personalizadas

**Frontend** — Dos enfoques:
1. **Componentes y hooks:** Testing Library, pruebas de renderizado e interacción
2. **Contexto y API:** Pruebas de estado global y llamadas HTTP

### Resultados Globales

| Módulo | Tests | Fallos | Cobertura JaCoCo | ¿Cumple ≥60%? |
|--------|:-----:|:------:|:----------------:|:-------------:|
| **ms-ventas** | 86 | 0 | **91%** | ✅ Sí |
| **ms-datos-org** | 22 | 0 | **99%** | ✅ Sí |
| **ms-indicadores** | 38 | 0 | **62%** | ✅ Sí |
| **bff** | 28 | 0 | **62%** | ✅ Sí |
| **api-gateway** | 6 | 0 | **89%** | ✅ Sí |
| **Backend total** | **180** | **0** | **—** | **✅ TODOS** |
| Frontend (Vitest) | 45 | 0 | — | — |
| **Gran Total** | **225** | **0** | **—** | — |

### Desglose de Tests por Tipo (ms-ventas - 86 tests)

| Tipo de Prueba | Archivo | Tests | Qué prueba |
|---------------|---------|:-----:|------------|
| Service | `VentaServiceTest` | 9 | Lógica de ventas con Mockito |
| Service | `ProductoServiceTest` | 11 | CRUD productos, stock, imágenes |
| Service | `SucursalServiceTest` | 7 | Gestión sucursales |
| Service | `ReporteServiceTest` | 6 | Reportes y cálculos |
| Service | `ClimaServiceTest` | 4 | API clima con mock |
| Service | `MindicadorServiceTest` | 5 | API indicadores con mock |
| Service | `AjustePrecioServiceTest` | 4 | Ajuste por UF |
| Controller | `ReporteControllerTest` | 8 | 8 endpoints de reportes |
| Controller | `IndicadorControllerTest` | 8 | Endpoints indicadores (ms-indicadores) |
| Controller | `AuthControllerTest` | 4 | Login, roles, JWT (bff) |
| Controller | `TicketControllerTest` | 6 | CRUD tickets (bff) |
| Repository | `VentaRepositoryImplTest` | 8 | Consultas SQL personalizadas |
| Builder | `VentaBuilderTest` | 4 | Patrón Builder |
| Events | `VentaEventListenerTest` | 1 | Evento venta creada |
| Events | `StockUpdateListenerTest` | 2 | Evento stock actualizado |
| Config | `DataInitializerTest` | 5 | Carga de datos iniciales |
| Exception | `GlobalExceptionHandlerTest` | 2 | Manejo global errores |
| Security | `JwtUtilTest` | 5 | Token JWT generación/validación (bff) |

### ¿Cómo los patrones de diseño mejoran la mantenibilidad?

| Patrón | Dónde se aplica | Beneficio en mantenibilidad |
|--------|----------------|-----------------------------|
| **Strategy** | `ms-indicadores` — 4 estrategias de cálculo (ventas, rentabilidad, inventario, ticket promedio) | Agregar un nuevo indicador = crear una nueva clase Strategy, sin modificar el código existente |
| **Factory** | `ms-indicadores` — crea la Strategy según el tipo de indicador | Centraliza la creación, evita switch/case dispersos |
| **Builder** | `ms-ventas` — `VentaBuilder` construye ventas con validaciones | Encapsula la lógica de construcción, evita constructores gigantes |
| **DTO** | Todos los módulos — separa entidades JPA de la respuesta API | Cambios en la BD no afectan la API, y viceversa |
| **Circuit Breaker** | `BFF` — resilience4j en llamadas a microservicios | Un microservicio caído no bloquea todo el sistema |
| **BFF** | `bff` — backend específico para el frontend | El frontend solo conoce 1 API, los cambios internos no lo afectan |
| **Template Method** | `ms-ventas` — ReporteService con consultas reusables | Nueva consulta = nuevo método sin duplicar SQL |

### Cobertura Mínima del 60% — Evidencia
```
ms-ventas      ████████████████████░░  91%   > 60% ✅
ms-datos-org   ██████████████████████  99%   > 60% ✅
api-gateway    ████████████████████░░  89%   > 60% ✅
bff            ██████████████░░░░░░░░  62%   ≥ 60% ✅
ms-indicadores ██████████████░░░░░░░░  62%   ≥ 60% ✅
               ──────────────────────
Mínimo exigido:                    60%   TODOS CUMPLEN ✅
```

### Cómo se genera y verifica la cobertura
1. JaCoCo se ejecuta automáticamente con `mvn clean test` (plugin en cada `pom.xml`)
2. Reporte HTML en `target/site/jacoco/index.html` — pincha cada clase para ver línea por línea
3. Verificación en CI (puede fallar el build si cobertura < 60%)

---

## Slide 8: Demo en Vivo (3 min) → **Indicador 7**

### Paso a paso de la demo

```
┌──────────────────────────────────────────────────────────────────┐
│                          DEMO EN VIVO                            │
└──────────────────────────────────────────────────────────────────┘

1. FRONTEND TESTS (30s)
   cd frontend
   npm test
   → 45 passed, 0 failed

2. BACKEND TESTS (30s)
   cd ms-ventas
   .\mvnw.cmd clean test
   → 86 tests, BUILD SUCCESS

3. COBERTURA JaCoCo (30s)
   Abrir: ms-ventas/target/site/jacoco/index.html
   → 91% cobertura, línea por línea

4. CONTENEDORES DOCKER (15s)
   docker compose ps
   → 6 contenedores UP

5. LOGIN + ENDPOINTS (30s)
   curl -X POST localhost:9084/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin123"}'
   → Token JWT recibido

6. APIs EXTERNAS (30s)
   curl localhost:9084/api/economico/indicadores
   → UF=$40.801, USD=$905,78 (REAL)

   curl localhost:9084/api/clima/sucursales
   → 9 ciudades con clima REAL

7. FRONTEND EN NAVEGADOR (15s)
   localhost:5173 → login → dashboard → reportes
```

### Evidencia de Funcionalidad y Escalabilidad
- **Funcionalidad:** 58 endpoints probados, frontend con 10 páginas funcionales
- **Escalabilidad:** Cada microservicio escala independientemente (múltiples instancias)
- **Tolerancia a fallos:** Circuit Breaker — si un micro cae, el resto sigue funcionando
- **Portabilidad:** Docker Compose — 1 comando levanta todo el sistema

---

## Slide 9: Versionamiento GitHub (30s)

### 6 Repositorios Separados por Componente
| Repositorio | URL | Tecnología |
|------------|-----|-----------|
| **Principal** | `github.com/Basty66/cordillera` | Docker, docs |
| **Frontend** | `github.com/Basty66/ms-ventas-frontend` | React 19 |
| **BFF** | `github.com/Basty66/ms-ventas-bff` | Spring Boot |
| **ms-ventas** | `github.com/Basty66/ms-ventas` | Spring Boot |
| **ms-datos-org** | `github.com/Basty66/ms-datos-org` | Spring Boot |
| **ms-indicadores** | `github.com/Basty66/ms-indicadores` | Spring Boot |
| **API Gateway** | `github.com/Basty66/ms-ventas-gateway` | Spring Cloud |

### Release Entregable
- **Release:** v1.0.0 en `github.com/Basty66/cordillera/releases`
- **Archivo:** `ms-ventas-v1.0.0.zip` (173 MB)
- **Contenido:** Frontend, 5 módulos backend, docker-compose, documentación

---

## Slide 10: Cierre (30s)

### Resumen de Logros
| Indicador | Logro |
|-----------|-------|
| Arquitectura | BFF + Gateway + **3 microservicios** (exige 2) |
| Frontend | React 19 + Vite + Tailwind — **10 páginas funcionales** |
| Persistencia | JPA/Hibernate + **PostgreSQL Neon cloud** + DataInitializer |
| Pruebas | **225 tests, 0 fallos** |
| Cobertura | **91% / 99% / 89% / 62% / 62%** — todos ≥60% ✅ |
| APIs externas | **mindicador.cl** + **OpenWeatherMap** — datos reales |
| Patrones | **Strategy, Factory, Builder, BFF, Gateway, Circuit Breaker** |
| GitHub | **6 repositorios** + release ZIP |
| Docker | **6 contenedores**, puertos 908x |

### Preguntas para la defensa (preparadas)
Ver sección siguiente.

---

## PREGUNTAS PARA LA DEFENSA

### 1. ¿Por qué microservicios y no un monolito? (Indicador 5)
> Porque cada dominio tiene distintos requisitos de escalabilidad. ej: ms-ventas recibe más carga que ms-datos-org. Con microservicios escalamos solo lo necesario. Además, equipos independientes pueden trabajar en paralelo, y un fallo en ventas no afecta indicadores.

### 2. ¿Qué es un BFF y por qué lo usaste? (Indicador 1)
> BFF (Backend For Frontend) es un backend intermedio entre el frontend y los microservicios. Lo usamos para que el frontend solo conozca UNA API. El BFF se encarga de orquestar datos de múltiples micros (dashboard), manejar autenticación JWT, y transformar respuestas. Sin BFF, el frontend tendría que llamar a 3 APIs diferentes.

### 3. ¿Cómo aseguras la comunicación entre servicios? (Indicador 3)
> El frontend solo llama al API Gateway. El Gateway enruta según el path a cada microservicio. El BFF usa RestTemplate con Circuit Breaker para llamar a los micros internamente. Cada llamada tiene timeout de 5s, fallback con valores por defecto, y resilience4j protege contra fallos en cascada.

### 4. ¿Cómo implementaste la persistencia de datos? (Indicador 3)
> JPA/Hibernate con PostgreSQL Neon cloud. Cada microservicio tiene su propio esquema de base de datos (Database per Service). Las entidades JPA se mapean automáticamente con `ddl-auto: update`. Datos iniciales se cargan con DataInitializer que verifica si ya existen antes de insertar. BFF usa H2 para auth y tickets porque no requiere persistencia a largo plazo.

### 5. ¿Cómo lograste cobertura ≥60% en todos los módulos? (Indicador 4)
> JaCoCo está configurado en cada pom.xml. Escribimos tests en 3 capas: servicios con Mockito, controladores con MockMvc, repositorios con @DataJpaTest. Cada test unitario cubre casos felices y casos borde. El reporte HTML muestra línea por línea qué se ejecutó y qué no.

### 6. ¿Qué patrones de diseño aplicaste y cómo mejoran la mantenibilidad? (Indicador 8)
> 1. **Strategy**: en ms-indicadores, 4 algoritmos de cálculo intercambiables. Para agregar un nuevo indicador solo creamos una clase nueva.
> 2. **Factory**: crea la Strategy según el tipo de indicador, centraliza la lógica de creación.
> 3. **Builder**: VentaBuilder construye objetos Venta complejos con validación paso a paso.
> 4. **BFF**: el frontend no se afecta si cambia la topología interna.
> 5. **Circuit Breaker**: un microservicio caído no bloquea todo el sistema.
> 6. **DTO**: cambios en la BD no afectan la API expuesta.

### 7. ¿Cómo demostrarías que la solución escala? (Indicador 7)
> 1. Cada microservicio puede tener múltiples instancias (solo cambiar puerto y replicar).
> 2. API Gateway distribuye carga entre instancias.
> 3. La base de datos Neon cloud escala automáticamente.
> 4. Los circuit breakers evitan fallos en cascada si un micro está saturado.
> 5. Con Docker Compose podemos levantar 2 instancias de ms-ventas en segundos.

### 8. ¿Cuáles son las tecnologías usadas y cómo se integran? (Indicador 6)
> Frontend: React 19 con Vite y Tailwind. Backend: Java 21 con Spring Boot 3.2. Persistencia: JPA/Hibernate con PostgreSQL. Testing: JUnit 5, Mockito, Vitest. Infra: Docker Compose. APIs externas: mindicador.cl y OpenWeatherMap. Se integran via REST con JSON, el Gateway unifica el acceso, y Docker las empaqueta y orquesta.
