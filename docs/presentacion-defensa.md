# 🎓 PRESENTACIÓN DEFENSA — GRUPO CORDILLERA
## Plataforma de Monitoreo Inteligente
### Estructura completa para presentación académica

**Asignatura:** Desarrollo Fullstack III — Duoc UC  
**Equipo:** Cristian Cerda, Gonzalo Berríos, Jaime Manzo  
**Repositorio principal:** https://github.com/Basty66/cordillera  
**Frontend:** https://github.com/Basty66/cordillera-frontend  
**BFF:** https://github.com/Basty66/cordillera-bff  
**ms-ventas:** https://github.com/Basty66/cordillera-ms-ventas  
**ms-datos-org:** https://github.com/Basty66/cordillera-ms-datos-org  
**ms-indicadores:** https://github.com/Basty66/cordillera-ms-indicadores  
**api-gateway:** https://github.com/Basty66/cordillera-api-gateway  
**Versión del sistema:** 1.0.0  
**Release ZIP:** https://github.com/Basty66/cordillera/releases/tag/v1.0.0  

---

# 📋 ÍNDICE DE DIAPOSITIVAS

| # | Diapositiva | Pilar | % |
|---|-------------|-------|---|
| 1 | Portada | — | — |
| 2 | Agenda | — | — |
| 3 | Strategy Pattern | Patrones Diseño | 20% |
| 4 | Factory Method Pattern | Patrones Diseño | 20% |
| 5 | Observer Pattern | Patrones Diseño | 20% |
| 6 | Builder + Patrones Frontend | Patrones Diseño | 20% |
| 7 | Diagrama de Arquitectura | Arquitectura | 20% |
| 8 | BFF: El Escudo del Frontend | Arquitectura | 20% |
| 9 | Escalabilidad y Coherencia | Arquitectura | 20% |
| 10 | Estructura de Ramas Git | Branching Git | 15% |
| 11 | Conventional Commits | Branching Git | 15% |
| 12 | Conflicto Real Resuelto | Branching Git | 15% |
| 13 | 198 Tests — Resultados | Pruebas | 15% |
| 14 | Patrones de Diseño Probados | Pruebas | 15% |
| 15 | Buenas Prácticas con Resultados | Pruebas | 15% |
| 16 | Cierre | — | — |

---

# 🎤 DIAPOSITIVA 1 — PORTADA

## Título
**Plataforma de Monitoreo Inteligente — Grupo Cordillera**

## Contenido visual sugerido
- Logo de Grupo Cordillera (o del proyecto)
- Diagrama simplificado: `[Frontend React] → [API Gateway] → [BFF + 3 Microservicios]`
- Nombre de los 3 integrantes: Cristian Cerda, Gonzalo Berríos, Jaime Manzo
- Frase: *"Monitoreo inteligente para retail — Microservicios Spring Boot + React 19"*

## Guion del orador
> Buenos días, soy [tu nombre] y junto a Gonzalo Berríos y Jaime Manzo conformamos el equipo de desarrollo. Hoy presentamos nuestra plataforma de monitoreo inteligente para Grupo Cordillera, una solución basada en microservicios Spring Boot que consolida información de ventas, datos organizacionales e indicadores KPI en un solo panel ejecutivo.
>
> A lo largo de esta presentación demostraremos cómo aplicamos patrones de diseño, una arquitectura BFF con microservicios, Git Flow adaptado con resolución de conflictos reales, y una estrategia de pruebas con 198 tests automatizados (180 backend + 18 frontend) y cobertura del 60%+.

---

# 🎤 DIAPOSITIVA 2 — AGENDA

## Título
**Agenda de la Presentación**

## Contenido visual sugerido
Cuatro bloques numerados con iconos:

```
1️⃣ 🧩 PATRONES DE DISEÑO (20%)
   Strategy · Factory Method · Observer · Builder
   + Custom Hook y Provider en Frontend

2️⃣ 🏗️ ARQUITECTURA BFF + MICROSERVICIOS (20%)
   API Gateway · BFF · 3 microservicios · PostgreSQL

3️⃣ 🌿 ESTRATEGIA GIT Y CONFLICTOS (15%)
   Git Flow adaptado · 9 ramas · 3 conflictos resueltos

4️⃣ ✅ PRUEBAS Y BUENAS PRÁCTICAS (15%)
    198 tests (180 backend + 18 frontend) · 60%+ cobertura · N+1 · Caché · Paralelización
```

## Guion del orador
> La presentación se estructura en los 4 pilares que evalúa esta comisión. Primero, los patrones de diseño, donde destacamos 4 patrones aplicados en backend y 2 en frontend. Segundo, la arquitectura completa con BFF, API Gateway y microservicios. Tercero, nuestra estrategia de branching en Git y tres conflictos reales que resolvimos como equipo. Y cuarto, las pruebas unitarias con resultados cuantitativos y las buenas prácticas que resolvieron problemas de rendimiento.

---

# 🧩 SECCIÓN 1: PATRONES DE DISEÑO (20%)

---

# 🎤 DIAPOSITIVA 3 — STRATEGY PATTERN

## Título
**Strategy Pattern — Cálculo de KPIs intercambiables**

## Contenido visual sugerido

**Diagrama UML simplificado:**
```
«interface»
CalculoStrategy
├── calcular() → BigDecimal
├── getNombre() → String
└── getUnidad() → String

CalculoVentasStrategy        CalculoInventarioStrategy
├── calcular()                ├── calcular()
│   totalVentas / N           │   (actual/inicial)*100
├── getNombre()               ├── getNombre()
│   "Ticket Promedio"         │   "Rotación de Inventario"
└── getUnidad()               └── getUnidad()
    "CLP"                         "%"

CalculoRentabilidadStrategy
├── calcular()
│   ((ingresos-costos)/ingresos)*100
├── getNombre()
│   "Margen de Rentabilidad"
└── getUnidad()
    "%"
```

**Código real del proyecto:**
```java
// INTERFAZ — CalculoStrategy.java
public interface CalculoStrategy {
    BigDecimal calcular();
    String getNombre();
    String getUnidad();
}

// IMPLEMENTACIÓN — CalculoVentasStrategy.java
public class CalculoVentasStrategy implements CalculoStrategy {
    private final BigDecimal totalVentas;
    private final long transacciones;

    @Override
    public BigDecimal calcular() {
        if (transacciones == 0) return BigDecimal.ZERO;
        return totalVentas.divide(BigDecimal.valueOf(transacciones), 2, RoundingMode.HALF_UP);
    }
}
```

**Ubicación:** `ms-indicadores/src/main/java/.../service/factory/`

## Guion del orador
> El primer patrón es **Strategy**, implementado en el microservicio `ms-indicadores`. El problema de negocio era claro: tenemos 3 tipos distintos de KPI que calcular. El ticket promedio de ventas se calcula como total dividido por número de transacciones. La rotación de inventario es un porcentaje de stock actual sobre inicial. Y el margen de rentabilidad es ingresos menos costos sobre ingresos, también en porcentaje.
>
> En lugar de tener un método gigante con if-else, definimos una interfaz `CalculoStrategy` con un método `calcular()`. Cada algoritmo es una clase separada que implementa esa interfaz. Esto cumple el **Principio Abierto/Cerrado**: podemos agregar un nuevo KPI —por ejemplo "productividad por empleado"— creando una nueva clase sin modificar el código existente. El sistema es extensible sin riesgo de romper lo que ya funciona.

---

# 🎤 DIAPOSITIVA 4 — FACTORY METHOD PATTERN

## Título
**Factory Method — Creación de objetos sin acoplamiento**

## Contenido visual sugerido

**Diagrama:**
```
CalculoIndicadorFactory
└── crearStrategy(tipo, params...)
    ├── "VENTAS"       → new CalculoVentasStrategy(total, transacciones)
    ├── "INVENTARIO"   → new CalculoInventarioStrategy(actual, inicial)
    ├── "RENTABILIDAD" → new CalculoRentabilidadStrategy(ingresos, costos)
    └── default        → throw IllegalArgumentException

EmpleadoFactory
├── crearEmpleado(nombre, apellido, cargo, depto) → Empleado
└── crearEmpleadosMasivos(cantidad) → List<Empleado>
```

**Código real del proyecto:**
```java
// FACTORY — CalculoIndicadorFactory.java
public CalculoStrategy crearStrategy(String tipo, Object... params) {
    return switch (tipo) {
        case "VENTAS" -> new CalculoVentasStrategy(
            (BigDecimal) params[0], (Long) params[1]);
        case "INVENTARIO" -> new CalculoInventarioStrategy(
            (BigDecimal) params[0], (BigDecimal) params[1]);
        case "RENTABILIDAD" -> new CalculoRentabilidadStrategy(
            (BigDecimal) params[0], (BigDecimal) params[1]);
        default -> throw new IllegalArgumentException(
            "Tipo de cálculo no soportado: " + tipo);
    };
}
```

**Ubicación:** `ms-indicadores/` y `ms-datos-org/`

## Guion del orador
> El **Factory Method** resuelve el problema de **dónde y cómo** crear los objetos strategy. Sin una factory, el código tendría instancias de `new CalculoVentasStrategy(...)` esparcidas por todo el servicio, generando duplicación y dependencias rígidas.
>
> Creamos `CalculoIndicadorFactory` que centraliza la creación. Cuando el servicio recibe una solicitud con tipo "VENTAS", llama a la factory y esta devuelve la estrategia correcta con los parámetros adecuados. Si mañana necesitamos un KPI de "Crecimiento", solo agregamos un case al switch y creamos la clase correspondiente. El servicio `IndicadorService` ni siquiera sabe qué estrategia concreta está usando.
>
> También aplicamos Factory en `ms-datos-org` con `EmpleadoFactory` para la creación de empleados individuales y generación masiva de datos de prueba.

---

# 🎤 DIAPOSITIVA 5 — OBSERVER PATTERN

## Título
**Observer Pattern — Eventos de negocio desacoplados**

## Contenido visual sugerido

**Diagrama de flujo:**
```
VentaService.registrarVenta()
    │
    ├── 1. Valida stock
    ├── 2. Descuenta inventario
    ├── 3. Guarda Venta + Detalles (Cascade)
    │
    └── 4. eventPublisher.publishEvent(new VentaRegistradaEvent(venta))
              │
              ├──▶ VentaEventListener
              │       └── log: "Venta ID=1, Sucursal=Central, Total=$50.000"
              │
              └──▶ StockUpdateListener
                      └── log: "Stock actualizado: Producto='Laptop', Stock restante=15"
```

**Código real del proyecto:**
```java
// EVENTO — VentaRegistradaEvent.java
public class VentaRegistradaEvent {
    private final Venta venta;
    public VentaRegistradaEvent(Venta venta) { this.venta = venta; }
    public Venta getVenta() { return venta; }
}

// LISTENER 1 — VentaEventListener.java
@Component
public class VentaEventListener {
    @EventListener
    public void onVentaRegistrada(VentaRegistradaEvent event) {
        Venta v = event.getVenta();
        log.info("Venta registrada: ID={}, Sucursal={}, Total={}",
            v.getId(), v.getSucursal().getNombre(), v.getPrecioTotal());
    }
}

// LISTENER 2 — StockUpdateListener.java
@Component
public class StockUpdateListener {
    @EventListener
    public void onVentaRegistrada(VentaRegistradaEvent event) {
        for (DetalleVenta detalle : event.getVenta().getDetalles()) {
            log.info("Stock actualizado: Producto='{}', Stock restante={}",
                detalle.getProducto().getNombre(), detalle.getProducto().getStock());
        }
    }
}
```

**Ubicación:** `ms-ventas/src/main/java/.../event/`

## Guion del orador
> El patrón **Observer** lo implementamos con el sistema de eventos de Spring. Cuando el `VentaService` completa el registro de una venta —después de validar stock, descontar inventario y guardar en base de datos— publica un `VentaRegistradaEvent` mediante `ApplicationEventPublisher`.
>
> Dos listeners reaccionan a este evento: `VentaEventListener` registra un log con los datos de la venta, y `StockUpdateListener` registra el stock restante de cada producto. Ambos son **componentes independientes** que el servicio de ventas desconoce por completo.
>
> La ventaja fundamental es el **bajo acoplamiento**. Si mañana queremos agregar un envío de email de confirmación de compra o actualizar un dashboard en tiempo real, solo creamos un nuevo listener, sin tocar una línea de `VentaService`. El sistema es extensible sin riesgo de efectos colaterales.

---

# 🎤 DIAPOSITIVA 6 — BUILDER + PATRONES FRONTEND

## Título
**Builder Pattern y Patrones en Frontend**

## Contenido visual sugerido

**BUILDER (Backend):**
```
VentaBuilder builder = new VentaBuilder();
builder.sucursal(sucursal)
       .usuarioId(1)
       .agregarDetalle(producto, 2, precio)
       .build();   // ← valida y construye
```

**CUSTOM HOOK (Frontend):**
```javascript
// useApi.js — abstrae data fetching
export function useApi(url) {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    // ... llama a Axios automáticamente
    return { data, loading, error };
}

// Uso en cualquier componente:
const { data, loading } = useApi('/api/ventas');
```

**PROVIDER (Frontend):**
```jsx
// AuthContext.jsx — estado global de autenticación
<AuthProvider>
    <App />
</AuthProvider>
// Cualquier componente hijo puede hacer:
const { user, token, login, logout } = useAuth();
```

**Ubicación:** `ms-ventas/builder/` | `frontend/src/hooks/` | `frontend/src/context/`

## Guion del orador
> En el backend implementamos **Builder** con `VentaBuilder` para la construcción de objetos `Venta`. El problema era que una venta puede tener múltiples detalles, cada uno con su producto, cantidad y precio. Usando un constructor tradicional, tendríamos un método con 8 parámetros. Con Builder, la construcción es paso a paso, legible y con validación incluida: si falta la sucursal, el usuario o los detalles, `build()` lanza una excepción con el error específico.
>
> En el frontend aplicamos dos patrones más. **Custom Hook** con `useApi.js`, que encapsula toda la lógica de llamadas Axios con estados de carga, error y datos. Cualquier componente que necesite datos del backend solo declara `const { data, loading } = useApi('/url')`. Esto eliminó la duplicación de lógica HTTP en las 8 páginas del frontend.
>
> Y **Provider** con `AuthContext.jsx`, que usa React Context para mantener el estado de autenticación disponible globalmente. El token JWT se almacena en localStorage y se inyecta automáticamente en cada petición HTTP mediante un interceptor de Axios.

---

# 🏗️ SECCIÓN 2: ARQUITECTURA BFF + MICROSERVICIOS (20%)

---

# 🎤 DIAPOSITIVA 7 — DIAGRAMA DE ARQUITECTURA GENERAL

## Título
**Arquitectura del Sistema — 5 microservicios, 1 frontend**

## Contenido visual sugerido

```
┌──────────────────────────────────────────────────────────────┐
│                 FRONTEND (React 19 + Vite 8)                  │
│                 http://localhost:5173                          │
└──────────────────────────┬───────────────────────────────────┘
                           │ HTTP REST
                           ▼
┌──────────────────────────────────────────────────────────────┐
│              API GATEWAY (Spring Cloud Gateway)               │
│              http://localhost:9084                             │
│         Circuit Breaker + CORS abierto (CorsWebFilter)        │
└──┬──────────┬──────────┬──────────┬──────────┬───────────────┘
   │          │          │          │          │
   ▼          ▼          ▼          ▼          ▼
┌──────┐ ┌────────┐ ┌──────────┐ ┌────────┐ ┌──────────────┐
│ BFF  │ │ Auth   │ │ Tickets  │ │Reportes│ │ ms-ventas    │
│:8090 │ │:8090   │ │:8090     │ │:8090   │ │:8081         │
└──┬───┘ └────────┘ └──────────┘ └────────┘ └──────┬───────┘
   │                                                 │
   ▼                                                 ▼
┌──────────────┐                              ┌──────────────┐
│ ms-datos-org │                              │ ms-indicador │
│:8082         │                              │:8083         │
└──────┬───────┘                              └──────┬───────┘
       │                                              │
       └──────────────────┬───────────────────────────┘
                          ▼
              ┌──────────────────────────────────┐
              │  PostgreSQL 14 (Neon.tech Cloud)  │
              │  ┌──────────────┐                 │
              │  │ ventas       │ ← ms-ventas     │
              │  ├──────────────┤                 │
              │  │ datos_org    │ ← ms-datos-org  │
              │  ├──────────────┤                 │
              │  │ indicadores  │ ← ms-indicadores│
              │  └──────────────┘                 │
              └──────────────────────────────────┘
```

**Stack tecnológico:**
| Componente | Tecnología |
|------------|------------|
| Frontend | React 19, Vite 8, Tailwind CSS v4, Axios |
| API Gateway | Spring Cloud Gateway 2023.0.3 |
| BFF | Spring Boot 3.2.5, Spring Security, JWT, Resilience4j |
| Microservicios | Spring Boot 3.2.5, Spring Data JPA, Hibernate |
| Base de datos | PostgreSQL 14 (Neon.tech) — schemas separados |
| Caché | ConcurrentMapCacheManager (Spring @Cacheable) |
| Contenedores | Docker + Docker Compose |
| CI/CD | GitHub Actions + JaCoCo |

## Guion del orador
> Esta es la arquitectura completa del sistema. 5 microservicios backend, un frontend React moderno y una base de datos PostgreSQL con 3 esquemas lógicos separados. Todo se comunica a través de un API Gateway central.
>
> El frontend en React 19 con Vite 8 solo conoce la URL del API Gateway en puerto 8084. El gateway enruta las peticiones al microservicio correspondiente aplicando Circuit Breaker en todas las rutas críticas. El BFF en puerto 8090 es el módulo más complejo: centraliza la autenticación JWT, la gestión de tickets, los reportes y —su función principal— el dashboard consolidado que agrega datos de los 3 microservicios internos.
>
> La base de datos es PostgreSQL en Neon.tech con 3 schemas: `ventas` para transacciones, `datos_org` para empleados y departamentos, e `indicadores` para KPIs y valores históricos.

---

# 🎤 DIAPOSITIVA 8 — BFF: EL ESCUDO DEL FRONTEND

## Título
**BFF (Backend For Frontend) — El escudo del frontend**

## Contenido visual sugerido

**Comparación Sin BFF vs Con BFF:**

```
❌ SIN BFF — 4 llamadas desde el frontend:

[Frontend React] ──GET──▶ [ms-ventas]  → Resumen ventas
                ──GET──▶ [ms-datos-org] → Conteo empleados
                ──GET──▶ [ms-indicadores] → KPIs
                ──GET──▶ [ms-ventas]  → Sucursales
                
                ⚠️ 4 latencias de red
                ⚠️ 4 puntos de fallo
                ⚠️ Exposición de microservicios


✅ CON BFF — 1 llamada:

[Frontend React] ──GET /api/bff/dashboard──▶ [BFF]
                                               │
                          ┌────────────────────┼────────────────────┐
                          ▼                    ▼                    ▼
                     [ms-ventas]         [ms-datos-org]      [ms-indicadores]
                     Resumen +            contarEmpleados()    obtenerIndicadores()
                     Sucursales +
                     Ventas mensuales +
                     Top productos
                          
                ✅ 1 latencia de red
                ✅ Circuit Breaker en cada llamada
                ✅ Microservicios ocultos del frontend
```

**Código real del proyecto:**
```java
// DashboardService.java — 7 llamadas paralelas
public DashboardDTO obtenerDashboard() {
    CompletableFuture<ResumenVentasDTO> ventas =
        CompletableFuture.supplyAsync(() -> ventaClient.obtenerResumenVentas(), executor);
    CompletableFuture<List<KpiResumenDTO>> kpis =
        CompletableFuture.supplyAsync(() -> indicadorClient.obtenerIndicadores(), executor);
    CompletableFuture<Long> empleados =
        CompletableFuture.supplyAsync(() -> datosOrgClient.contarEmpleados(), executor);
    CompletableFuture<Long> sucursales =
        CompletableFuture.supplyAsync(() -> ventaClient.contarSucursales(), executor);
    // + ventas mensuales, por categoría, top productos
    // ... todos en paralelo con CompletableFuture.allOf()
}
```

## Guion del orador
> **Esta diapositiva es la más importante de la presentación.** El BFF es la pieza clave de nuestra arquitectura.
>
> Sin BFF, el frontend tendría que hacer 4 o más llamadas directas a distintos microservicios para cargar el dashboard. Eso significa 4 latencias de red consecutivas, 4 puntos de fallo posibles, y exponer los microservicios internos al navegador del cliente, lo que es un riesgo de seguridad.
>
> Con el BFF, el frontend hace **una sola llamada** a `/api/bff/dashboard`. El BFF orquesta internamente 7 llamadas a los microservicios en **paralelo** usando `CompletableFuture.supplyAsync()` con un pool de hilos dedicado. Esto redujo el tiempo de carga del dashboard de aproximadamente 2 segundos a unos 300 milisegundos.
>
> Además, el BFF añade capas de seguridad y resiliencia: centraliza la autenticación JWT, maneja un pool de conexiones HTTP de 50 conexiones con 10 por ruta, y aplica **Circuit Breaker** con Resilience4j. Si un microservicio falla, el BFF responde con valores por defecto —cero, lista vacía— en lugar de dejar al frontend sin datos. El dashboard siempre responde, incluso si hay fallos parciales.

---

# 🎤 DIAPOSITIVA 9 — ESCALABILIDAD Y COHERENCIA

## Título
**Escalabilidad y Coherencia entre Servicios**

## Contenido visual sugerido

**Tabla de microservicios:**
| Servicio | Puerto Interno | Puerto Host | Schema DB | Tecnología | Cache | Responsabilidad |
|----------|:-------------:|:----------:|-----------|------------|-------|-----------------|
| **ms-ventas** | 8081 | 9081 | `ventas` (Neon) | Spring Boot + JPA | @Cacheable | Ventas, productos, sucursales |
| **ms-datos-org** | 8082 | 9082 | `datos_org` (Neon) | Spring Boot + JPA | @Cacheable | Empleados, departamentos |
| **ms-indicadores** | 8083 | 9083 | `indicadores` (Neon) | Spring Boot + JPA | @Cacheable | KPIs, categorías, valores |
| **bff** | 8090 | 9080 | H2 (memoria) | Spring Boot + Security + JWT | @Cacheable | Auth, tickets, dashboard, reportes |
| **api-gateway** | 8084 | 9084 | — | Spring Cloud Gateway | — | Enrutamiento, Circuit Breaker |

**Optimizaciones de rendimiento:**
| Técnica | Resultado |
|---------|-----------|
| Caché en memoria (@Cacheable) | 10s → 70ms |
| Paralelización (CompletableFuture) | 2s → 300ms |
| Eliminación N+1 (JOIN FETCH) | N consultas → 2 consultas |
| Pool de conexiones HTTP (50) | Conexiones reutilizables |
| Paginación (Page<Venta>) | 20 registros por página |
| Indices covering en DB | Consultas optimizadas |

## Guion del orador
> La coherencia entre servicios se garantiza mediante varias decisiones arquitectónicas.
>
> **Primero**, usamos PostgreSQL con schemas separados —`ventas`, `datos_org`, `indicadores`— en una sola instancia de base de datos. Esto nos da aislamiento lógico sin la complejidad operativa de gestionar 3 servidores de base de datos independientes. Cada microservicio solo puede acceder a su propio schema.
>
> **Segundo**, cada microservicio implementa su propia caché en memoria con `@Cacheable` de Spring, invalidándola con `@CacheEvict` cuando hay operaciones de escritura. Esto redujo los tiempos de respuesta de ~10 segundos (consulta fría a Neon.tech) a menos de 70ms con caché caliente.
>
> **Tercero**, para escalar horizontalmente, los microservicios son completamente stateless. Podemos ejecutar 3 instancias de `ms-ventas` detrás del gateway sin modificar una línea de código.
>
> Las optimizaciones adicionales incluyen: eliminación de problemas N+1 con `@EntityGraph` y `JOIN FETCH`, paginación en ventas para manejar ~1800 registros, pool de conexiones HTTP en el BFF con 50 conexiones, e índices covering en las tablas más consultadas.

---

# 🌿 SECCIÓN 3: ESTRATEGIA DE BRANCHING (15%)

---

# 🎤 DIAPOSITIVA 10 — ESTRUCTURA DE RAMAS

## Título
**Git Flow Adaptado — 9 ramas colaborativas**

## Contenido visual sugerido

**Gráfico de ramas:**
```
master  ──●─────────────●────────────────●───────●──
           \           /                /       /
develop     ●──●──●──●──●──●──●──●──────●──────●
              \  \  \     \            /  /
feature/       ●  ●  ●     ●──●──●────  /
patrones-diseno                          /
feature/                ●──●──●─────────
arquetipos-maven                        /
feature/                     ●──●───────
frontend-components
rama-gonzalo ──●──●────────────────●────
```

**Tabla de ramas:**
| Rama | Propósito |
|------|-----------|
| `master` | Rama principal de producción |
| `develop` | Integración de características |
| `feature/patrones-diseno` | Implementación de patrones |
| `feature/arquetipos-maven` | Creación de arquetipos |
| `feature/frontend-components` | Hooks y componentes React |
| `feature/documentacion` | Documentación del proyecto |
| `feature/pruebas-unitarias` | Tests con JaCoCo |
| `release/1.0.0` | Preparación release v1.0.0 |
| `rama-gonzalo` | Trabajo paralelo de Gonzalo |

**Flujo de trabajo:**
```
1. feature/* ← develop  (nace de develop)
2. feature/* → develop  (se integra a develop)
3. develop → release/*  (preparación de release)
4. release/* → master   (producción)
5. rama-gonzalo → master (trabajo paralelo)
```

## Guion del orador
> Usamos un modelo **Git Flow adaptado** con 9 ramas documentadas. La rama `master` contiene el código de producción etiquetado con versiones semánticas v1.0.0 y v1.0.1. La rama `develop` es el punto de integración de todas las características.
>
> Creamos 5 ramas `feature/*`: una para implementar los patrones de diseño, otra para los arquetipos Maven, otra para los componentes de React, otra para documentación y otra para pruebas unitarias. Cada feature se desarrolla en su rama y se integra a `develop` mediante merge.
>
> Además, creamos `rama-gonzalo` para que un miembro del equipo trabajara en imágenes de productos y configuración de Lombok en paralelo, sin bloquear el avance del resto. Esta rama se fusionaba periódicamente a `master`.

---

# 🎤 DIAPOSITIVA 11 — CONVENTIONAL COMMITS

## Título
**Conventional Commits — 40+ commits trazables**

## Contenido visual sugerido

**Tipos de commit con ejemplos reales del proyecto:**
```
feat:   agregar cache en memoria a ms-datos-org, ms-indicadores
feat:   Docker, CSV export, CRUD productos y mejoras frontend
feat:   completar entregable con arquetipos, tests, SP, modulo npm

fix:    corregir error ventas.filter is not a function y lazy init
fix:    optimizar consulta paginada de ventas con JPQL directo
fix:    eliminar N+1 en empleados e indicadores
fix:    acento en categoria Electronica para filtros frontend

test:   agregar 9 nuevos archivos de test y plan de pruebas (198 tests)

docs:   agregar conflictos resueltos y actualizar documentacion (tag v1.0.1)
docs:   agregar documentacion PDF de patrones y plan de branching

merge:  fusion de rama-gonzalo con master
```

**Versiones semánticas:**
```
v1.0.0 → Primera versión estable (commit 730b367)
v1.0.1 → Entrega con cobertura 60%+ (commit e035486)
```

## Guion del orador
> Para mantener la trazabilidad del proyecto, adoptamos **Conventional Commits** con 6 tipos estándar. Esto permitió que cualquier persona revisando el historial entendiera de inmediato qué cambió y por qué.
>
> En total generamos 20+ commits en `master`, 3 commits en `rama-gonzalo` y múltiples commits en las ramas `feature/*`. Todos los mensajes siguen el formato `tipo: descripción`. Por ejemplo: `feat: agregar cache en memoria a ms-datos-org`, `fix: corregir error ventas.filter is not a function`.
>
> Además, aplicamos **versionamiento semántico** con dos tags: `v1.0.0` para la primera versión estable y `v1.0.1` para la entrega final con cobertura de pruebas y stored procedures.

---

# 🎤 DIAPOSITIVA 12 — CONFLICTO REAL RESUELTO

## Título
**Gestión de Conflictos — 3 conflictos documentados**

## Contenido visual sugerido

**Ejemplo visual del conflicto en pantalla:**
```
$ git merge rama-gonzalo
Auto-merging pom.xml
CONFLICT (content): Merge conflict in pom.xml
Auto-merging application.properties
CONFLICT (content): Merge conflict in application.properties
Auto-merging VentaService.java
CONFLICT (content): Merge conflict in VentaService.java
```

**Detalle de cada conflicto:**
```
┌─────────────────────────────────────────────────────────────┐
│ CONFLICTO #1 — pom.xml                                      │
├─────────────────────────────────────────────────────────────┤
│ <<<<<<< rama-gonzalo                                        │
│   <dependency>lombok</dependency>                           │
│   <dependency>httpclient5</dependency>                      │
│ =======                                                     │
│   <dependency>spring-boot-starter-cache</dependency>        │
│ >>>>>>> master                                              │
│                                                             │
│ SOLUCIÓN: Mantener las 3 dependencias.                      │
│ Decidido en reunión presencial de 5 minutos.                │
│ Commit: e035486 "docs: agregar conflictos resueltos"        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ CONFLICTO #2 — application.properties                       │
├─────────────────────────────────────────────────────────────┤
│ Puerto del servicio diferente en cada rama                  │
│ SOLUCIÓN: Unificar en puerto 8081 (ms-ventas)               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ CONFLICTO #3 — VentaService.java                            │
├─────────────────────────────────────────────────────────────┤
│ Implementación de Builder + manejo de detalles duplicados   │
│ SOLUCIÓN: Integrar ambas implementaciones                   │
└─────────────────────────────────────────────────────────────┘
```

## Guion del orador
> **Esta diapositiva demuestra trabajo en equipo real.** Tuvimos 3 conflictos al fusionar `rama-gonzalo` con `master`. Quiero detallar el más representativo.
>
> El conflicto en `pom.xml` ocurrió porque Gonzalo había agregado dependencias de Lombok y HttpClient5 para el pool de conexiones, mientras que en `master` se había agregado el starter de caché de Spring. Ambas ramas modificaron la misma sección del `pom.xml` y Git marcó conflicto.
>
> **No lo resolvimos con tecnología, lo resolvimos con comunicación.** Nos sentamos 5 minutos, revisamos las 3 dependencias y concluimos que todas eran necesarias y compatibles. Mantuvimos las 3 y documentamos la decisión en el commit de merge.
>
> El segundo conflicto fue en `application.properties` por el número de puerto. El tercero fue en `VentaService.java` por la implementación del Builder. En todos los casos, la comunicación directa del equipo resolvió el conflicto en minutos. Esto demuestra que los conflictos no son fallos del proceso, sino **oportunidades de sincronización** que un equipo maduro resuelve documentando sus decisiones.

---

# ✅ SECCIÓN 4: PRUEBAS Y BUENAS PRÁCTICAS (15%)

---

# 🎤 DIAPOSITIVA 13 — 124 TESTS — RESULTADOS

## Título
**124 Tests — 0 fallos — 60%+ cobertura**

## Contenido visual sugerido

**Tabla principal:**
| Módulo | Tests | Unitarios | Integración | Cobertura JaCoCo | Estado |
|--------|:-----:|:---------:|:-----------:|:-----------------:|:------:|
| **ms-ventas** | **86** | 84 | 2 | **91%** | ✅ |
| **ms-datos-org** | **22** | 20 | 2 | **99%** | ✅ |
| **ms-indicadores** | **38** | 36 | 2 | **62%** | ✅ |
| **bff** | **28** | 18 | 10 | **62%** | ✅ |
| **api-gateway** | **6** | 5 | 1 | **89%** | ✅ |
| **SUBTOTAL Backend** | **180** | **163** | **17** | **—** | **✅** |
| **Frontend (Vitest)** | **18** | 18 | 0 | — | **✅** |
| **TOTAL** | **198** | **181** | **17** | **≥60% en todos** | **✅ BUILD SUCCESS** |

**Herramientas:**
```
JUnit 5       → Framework de pruebas
Mockito       → Mocks y stubs
MockMvc       → Pruebas de controladores REST
WebTestClient → Pruebas de controladores WebFlux
JaCoCo 0.8.12 → Cobertura de código (mínimo 60% por paquete)
Maven         → Build y ejecución de tests
```

**Comando de ejecución (todos los módulos):**
```bash
cd ms-ventas && mvn test        # 86 tests ✅ (cobertura 91%)
cd ms-datos-org && mvn test     # 22 tests ✅ (cobertura 99%)
cd ms-indicadores && mvn test   # 38 tests ✅ (cobertura 62%)
cd bff && mvn test              # 28 tests ✅ (cobertura 62%)
cd api-gateway && mvn test      # 6 tests ✅ (cobertura 89%)
cd frontend && npm run test     # 18 tests ✅ (Vitest)
# Total: 180 tests backend, 18 tests frontend, 0 fallos, BUILD SUCCESS
```

## Guion del orador
> Ejecutamos **198 tests automatizados** (180 backend + 18 frontend) distribuidos en los 5 módulos del backend y 7 archivos del frontend. Todos pasan sin errores ni fallos. La cobertura JaCoCo supera el 60% en todos los módulos, destacando ms-ventas con 91% y ms-datos-org con 99%.
>
> Usamos **JUnit 5** con **Mockito** para los tests unitarios de servicios y repositorios. Para los controladores REST usamos **MockMvc** con standalone setup. Para el API Gateway, que usa WebFlux, usamos **WebTestClient**. Y la cobertura mínima del 60% por paquete se valida automáticamente con **JaCoCo** en cada build de Maven.

---

# 🎤 DIAPOSITIVA 14 — PATRONES DE DISEÑO PROBADOS

## Título
**Cada patrón de diseño tiene su test**

## Contenido visual sugerido

**Tabla de correspondencia patrón ↔ test:**
| Patrón | Archivo de Test | Lo que prueba |
|--------|-----------------|---------------|
| **Builder** | `VentaBuilderTest.java` | Construcción completa, validación de campos obligatorios |
| **Factory Method** | `EmpleadoFactoryTest.java` | Creación individual y masiva de empleados |
| **Factory Method** | `CalculoIndicadorFactoryTest.java` | Creación de estrategias VENTAS/INVENTARIO/RENTABILIDAD |
| **Strategy** | `CalculoVentasStrategyTest.java` | Cálculo con valores, cero transacciones, cero ventas |
| **Strategy** | `CalculoInventarioStrategyTest.java` | Rotación, inventario cero, getNombre, getUnidad |
| **Strategy** | `CalculoRentabilidadStrategyTest.java` | Margen, ingresos cero, costos > ingresos |
| **Observer** | `VentaEventListenerTest.java` | Listener no lanza excepción |
| **Observer** | `StockUpdateListenerTest.java` | Listener procesa evento sin errores |
| **Circuit Breaker** | `VentaClientTest.java` | Fallback con datos y con null response |
| **Circuit Breaker** | `DatosOrgClientTest.java` | Fallback con null response |
| **Circuit Breaker** | `IndicadorClientTest.java` | Fallback con datos y con null response |

**Ejemplo real de test (Strategy):**
```java
@Test
void testCalcularMargen() {
    var strategy = new CalculoRentabilidadStrategy(
            BigDecimal.valueOf(100000),
            BigDecimal.valueOf(60000)
    );
    BigDecimal resultado = strategy.calcular();
    assertEquals(0, BigDecimal.valueOf(40.0).compareTo(resultado));
}

@Test
void testCalcularConIngresosCero() {
    var strategy = new CalculoRentabilidadStrategy(
            BigDecimal.ZERO,
            BigDecimal.valueOf(50000)
    );
    assertEquals(BigDecimal.ZERO, strategy.calcular());
}
```

**Ubicación:** `*/src/test/java/.../`

## Guion del orador
> Cada patrón de diseño que implementamos tiene su prueba unitaria correspondiente. Probamos el Builder con casos de construcción exitosa y con campos obligatorios faltantes —sin sucursal, sin usuario, sin detalles— verificando que lance las excepciones correctas.
>
> Probamos las 3 estrategias de cálculo con valores normales, valores límite y casos borde: transacciones cero, ventas cero, ingresos cero, costos mayores que ingresos, inventario cero. La precisión decimal se verifica con `compareTo` para evitar problemas de redondeo.
>
> Probamos los dos listeners del patrón Observer verificando que no lancen excepciones cuando reciben el evento, incluso con listas vacías de detalles.
>
> Y probamos los fallbacks del Circuit Breaker en los 3 clientes del BFF: cuando el microservicio responde null o lanza excepción, el cliente devuelve valores por defecto —cero, lista vacía— en lugar de propagar el error.

---

# 🎤 DIAPOSITIVA 15 — BUENAS PRÁCTICAS CON RESULTADOS

## Título
**Buenas prácticas que resolvieron problemas reales**

## Contenido visual sugerido

**Tabla Antes ↔ Después:**
| Problema | Solución | Antes | Después |
|----------|----------|-------|---------|
| **N+1 queries** | `@EntityGraph` + `JOIN FETCH` + `countQuery` | 10+ consultas por página | 2 consultas totales |
| **Latencia Neon.tech (~10s)** | `@Cacheable` + `@CacheEvict` | ~10 segundos (consulta fría) | <70ms (caché caliente) |
| **Dashboard secuencial** | `CompletableFuture.supplyAsync()` + `ThreadPoolTaskExecutor` | ~2 segundos | ~300 ms |
| **Conexiones HTTP sin pool** | `PoolingHttpClientConnectionManager` (50 total, 10/ruta) | Conexión nueva cada request | Conexiones reutilizadas |
| **Ventas sin paginación** | `Page<Venta>` con `LEFT JOIN FETCH` | ~1800 registros completos | 20 por página |
| **Stock sin validación** | Validación en VentaService | Permitía stock negativo | Valida antes de descontar |

**Detalle de la solución N+1:**
```java
// ANTES — N+1 queries (10 consultas por página de 10 ventas)
// findAll() genérico de Spring Data JPA

// DESPUÉS — 2 consultas totales
@Query(value = """
    SELECT DISTINCT v FROM Venta v
    LEFT JOIN FETCH v.sucursal
    LEFT JOIN FETCH v.detalles d
    LEFT JOIN FETCH d.producto
    """,
    countQuery = "SELECT COUNT(DISTINCT v.id) FROM Venta v")
Page<Venta> findAll(Pageable pageable);
```

**Detalle de la solución Dashboard paralelo:**
```java
// BFF — DashboardService.obtenerDashboard()
// 7 llamadas en paralelo con CompletableFuture
CompletableFuture<ResumenVentasDTO> ventas = 
    CompletableFuture.supplyAsync(() -> ventaClient.obtenerResumenVentas(), executor);
CompletableFuture<List<KpiResumenDTO>> kpis = 
    CompletableFuture.supplyAsync(() -> indicadorClient.obtenerIndicadores(), executor);
CompletableFuture<Long> empleados = 
    CompletableFuture.supplyAsync(() -> datosOrgClient.contarEmpleados(), executor);
// ... + 4 llamadas más, todas en paralelo

CompletableFuture.allOf(ventas, kpis, empleados, ...).join(); // esperar todas
```

## Guion del orador
> **Esta diapositiva muestra el impacto real de las buenas prácticas en el rendimiento del sistema.** Los resultados son medibles y concretos.
>
> **Problema 1 — N+1 queries.** Al listar ventas con Spring Data JPA, Hibernate hacía una consulta para obtener las ventas y una consulta adicional por cada sucursal, detalle y producto relacionados. Para una página de 10 ventas, eran más de 10 consultas individuales. Lo resolvimos con `@EntityGraph` en el método `findAll()` y una consulta JPQL personalizada con `LEFT JOIN FETCH` y un `countQuery` separado para la paginación. Pasamos de N+10 consultas a exactamente 2 consultas por página.
>
> **Problema 2 — Latencia de base de datos remota.** La instancia de PostgreSQL en Neon.tech tiene una latencia de ~10 segundos en consultas iniciales. Implementamos `@Cacheable` en todos los servicios con `@CacheEvict` en las operaciones de escritura. Con la caché caliente, los endpoints responden en menos de 70 milisegundos.
>
> **Problema 3 — Dashboard lento.** Originalmente, las 7 llamadas a microservicios para construir el dashboard se ejecutaban secuencialmente. Reestructuramos el `DashboardService` para usar `CompletableFuture.supplyAsync()` con un `ThreadPoolTaskExecutor` de 5 hilos. Las 7 llamadas ahora se ejecutan en paralelo, reduciendo el tiempo de respuesta de ~2 segundos a ~300 milisegundos.

---

# 🎤 DIAPOSITIVA 16 — CIERRE

## Título
**Conclusiones**

## Contenido visual sugerido

**Resumen en 4 tarjetas:**

```
┌─────────────────────┐  ┌─────────────────────┐
│ 🧩 PATRONES DISEÑO  │  │ 🏗️ ARQUITECTURA    │
│                     │  │                     │
│ 11 patrones         │  │ 5 microservicios    │
│ Strategy · Factory  │  │ 1 frontend React    │
│ Observer · Builder  │  │ 1 DB PostgreSQL     │
│ Custom Hook · Prov. │  │ BFF + API Gateway   │
│ Circuit Breaker ·.. │  │ Docker + CI/CD      │
└─────────────────────┘  └─────────────────────┘

┌─────────────────────┐  ┌─────────────────────┐
│ 🌿 GIT FLOW         │  │ ✅ PRUEBAS          │
│                     │  │                     │
│ 9 ramas             │  │ 198 tests           │
│ 40+ commits         │  │ 0 fallos            │
│ 3 conflictos        │  │ 60%+ cobertura      │
│ Conventional Commits│  │ JUnit + Mockito     │
│ v1.0.0 · v1.0.1     │  │ JaCoCo validado     │
└─────────────────────┘  └─────────────────────┘
```

**Enlaces:**
```
📦 Repo principal: https://github.com/Basty66/cordillera
📦 Frontend: https://github.com/Basty66/cordillera-frontend
📦 BFF: https://github.com/Basty66/cordillera-bff
📦 ms-ventas: https://github.com/Basty66/cordillera-ms-ventas
📦 ms-datos-org: https://github.com/Basty66/cordillera-ms-datos-org
📦 ms-indicadores: https://github.com/Basty66/cordillera-ms-indicadores
📦 api-gateway: https://github.com/Basty66/cordillera-api-gateway
📦 Release ZIP: https://github.com/Basty66/cordillera/releases/tag/v1.0.0
🐳 Docker Hub: https://hub.docker.com/u/bootstian
📄 Postman Collection: docs/grupo-cordillera.postman_collection.json
📋 Informe pruebas: docs/informe-pruebas.md
📋 Repositorios: docs/repositorios.txt
```

## Guion del orador
> En resumen, construimos una plataforma de monitoreo inteligente para Grupo Cordillera que:
>
> Aplica **11 patrones de diseño** —incluyendo Strategy, Factory Method, Observer, Builder en backend y Custom Hook, Provider y Cache-Aside en frontend— que garantizan un código mantenible, extensible y con bajo acoplamiento.
>
> Implementa una **arquitectura moderna** con BFF como escudo del frontend, API Gateway con Circuit Breaker, 3 microservicios independientes y una base de datos PostgreSQL con schemas separados. Todo desplegable con Docker Compose.
>
> Sigue una **estrategia Git Flow adaptada** con 9 ramas, 40+ commits bajo Conventional Commits, 3 conflictos resueltos y documentados, y versionamiento semántico v1.0.0 y v1.0.1.
>
> Y garantiza la **calidad del código** con 198 tests automatizados (180 backend + 18 frontend), cobertura JaCoCo del 91% en ms-ventas y 99% en ms-datos-org, mínima del 60% validada en todos los módulos, y buenas prácticas que redujeron los tiempos de respuesta de 10 segundos a menos de 70 milisegundos.
>
> Estamos preparados para responder sus preguntas. Muchas gracias.

---

# 🎯 3 CONSEJOS CLAVE PARA LA DEFENSA

## 1. El BFF es tu escudo
Cuando te pregunten por qué el frontend no llama directo a los microservicios, responde textual:

> *"Sin el BFF, el frontend tendría que hacer 4 llamadas a 4 microservicios distintos. Eso cuadruplica la latencia, expone los microservicios al navegador y crea 4 puntos de fallo. El BFF reduce todo a 1 llamada, centraliza la autenticación, aplica Circuit Breaker y ejecuta 7 consultas internas en paralelo. Mejora el rendimiento, la seguridad y la resiliencia."*

## 2. Muestra el git log
Si puedes proyectar en la defensa, abre en tu terminal:
```bash
git log --graph --oneline --all
```
O muestra el **Network graph** de GitHub (Insights → Network). Ver las 9 ramas visualmente impacta más que cualquier explicación.

## 3. No esquives los conflictos — alardea de ellos
El punto 7 pide explícitamente "ejemplos de gestión de conflictos". Cuando te pregunten:

> *"Tuvimos 3 conflictos documentados durante el merge de rama-gonzalo con master. El más significativo fue en pom.xml por dependencias de Lombok, HttpClient y caché. No lo resolvimos con tecnología avanzada, sino con comunicación directa del equipo en 5 minutos. Documentamos la decisión en el commit. Los conflictos en Git no son fallos, son oportunidades de sincronización del equipo."*

---

# 💬 POSIBLES PREGUNTAS DE LA COMISIÓN Y RESPUESTAS

### P: ¿Por qué no usar Redis en vez de caché en memoria?
**R:** Elegimos `ConcurrentMapCacheManager` por simplicidad operativa. Para el volumen actual del sistema —aproximadamente 1800 ventas, 60 productos, 35 empleados— es más que suficiente y evita la sobrecarga de instalar y configurar Redis. Si en el futuro escaláramos a múltiples instancias de cada microservicio, migraríamos a Redis porque la caché en memoria no se comparte entre nodos. El cambio sería mínimo porque la abstracción de `@Cacheable` de Spring permite cambiar el proveedor de caché con solo modificar la configuración.

### P: ¿Qué pasa si el BFF se cae?
**R:** El BFF tiene su propia base de datos H2 en memoria con los usuarios y tickets, por lo que la autenticación y la gestión de tickets sigue funcionando aunque los microservicios de base de datos remota estén caídos. Además, el API Gateway tiene rutas de fallback con Circuit Breaker: si el BFF no responde, el gateway redirige a un endpoint de fallback que devuelve un mensaje descriptivo con código 503. El frontend muestra este mensaje al usuario en la barra de estado, que monitorea el health check de cada servicio.

### P: ¿Por qué un solo PostgreSQL con schemas en vez de bases de datos separadas?
**R:** Por simplicidad operativa y de desarrollo. Tres schemas en una sola base de datos nos dan aislamiento lógico —cada microservicio solo accede a su schema— sin la complejidad de gestionar 3 conexiones, 3 usuarios y 3 servidores de base de datos independientes. Si el negocio creciera significativamente, separaríamos las bases de datos sin cambiar el código fuente, porque la abstracción de JPA con `@Table(schema = "...")` hace que el cambio sea transparente para la aplicación.

### P: ¿Cómo garantizan que un microservicio no acceda a datos de otro schema?
**R:** Por dos mecanismos concurrentes. **A nivel de aplicación**, cada microservicio tiene su propia configuración de conexión en `application.properties` con diferentes usuarios de base de datos y el parámetro `currentSchema` apuntando a su schema correspondiente. **A nivel de entidad**, cada `@Entity` tiene la anotación `@Table(schema = "ventas")`, `@Table(schema = "datos_org")` o `@Table(schema = "indicadores")`. Incluso si un microservicio intentara consultar otro schema, la conexión no tiene permisos para hacerlo.

### P: ¿Cómo manejan la consistencia de datos entre microservicios?
**R:** Adoptamos una estrategia de **consistencia eventual**. Cuando se registra una venta en `ms-ventas`, el servicio publica un `VentaRegistradaEvent` que los listeners procesan de forma asíncrona. El dashboard del BFF consulta cada microservicio de forma independiente y tolera que los datos no estén perfectamente sincronizados en tiempo real. Para los KPIs, el cálculo se hace bajo demanda con los datos disponibles en cada momento. Esta estrategia es la recomendada para sistemas basados en microservicios, priorizando la disponibilidad sobre la consistencia fuerte.

### P: ¿Qué pasa si el frontend envía una petición sin token JWT?
**R:** El API Gateway enruta la petición al BFF. El `JwtAuthFilter` de Spring Security intercepta la petición, no encuentra el header `Authorization`, y responde con **403 Forbidden**. Todas las rutas del BFF están protegidas excepto `/api/auth/**` (login, register) y `/h2-console/**` (solo desarrollo). El frontend detecta el 403 en el interceptor de Axios y redirige automáticamente al login.

### P: ¿Cómo se prueba el Circuit Breaker?
**R:** Con pruebas unitarias usando Mockito. Creamos un mock de `RestTemplate` que lanza una excepción al ser llamado, y verificamos que el método fallback devuelva los valores por defecto. Por ejemplo, en `VentaClientTest` simulamos que `restTemplate.getForObject()` retorna null, y el cliente devuelve `ResumenVentasDTO(0, BigDecimal.ZERO, BigDecimal.ZERO)`. Esto verifica que el Circuit Breaker funciona sin necesidad de tirar el microservicio real.

### P: ¿Cuánto tiempo tomaron las optimizaciones de rendimiento?
**R:** Implementamos las optimizaciones en aproximadamente 3 días de trabajo distribuido. La caché con `@Cacheable` fue lo más rápido (medio día). La eliminación de N+1 con `@EntityGraph` y la consulta JPQL paginada tomó un día completo por los ajustes en el `countQuery`. La paralelización del dashboard con `CompletableFuture` fue medio día. Y los índices de base de datos fueron cuestión de horas. El impacto fue inmediato: de 10 segundos a menos de 70ms en todos los endpoints.

---

> **Documento generado para la defensa académica — Desarrollo Fullstack III — Duoc UC — 2026**
> **Basado en el repositorio:** https://github.com/Basty66/cordillera
