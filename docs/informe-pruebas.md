# Informe de Pruebas Unitarias

**Fecha:** Junio 2026
**Proyecto:** Plataforma de Monitoreo Inteligente — Grupo Cordillera

---

## Resumen

| Métrica | Resultado |
|---|---|---|
| **Total de tests ejecutados** | 196 (18 frontend + 178 backend) |
| **Tests pasados** | 196 |
| **Tests fallidos** | 0 |
| **Archivos de test** | 60 (18 frontend + 42 backend) |
| **Cobertura backend** | Ver tabla por módulo |

---

## Módulo Frontend (React + Vitest)

### Configuración

- **Framework:** Vitest v4.1.9 + jsdom
- **Librerías:** @testing-library/react, @testing-library/jest-dom, @testing-library/user-event
- **Setup:** `test-setup.js` con matchers de jest-dom

### Tests por Categoría

#### API (1 archivo, 4 tests)

| Test | Estado |
|---|---|
| Exporta todas las funciones API | ✅ |
| login retorna una promesa | ✅ |
| getVentasPaginadas retorna una promesa | ✅ |
| getDashboard retorna una promesa | ✅ |

#### Context (2 archivos, 8 tests)

| Test | Estado |
|---|---|
| AuthContext — Estado default no autenticado | ✅ |
| AuthContext — Login setea usuario y token | ✅ |
| AuthContext — Login lanza error en credenciales inválidas | ✅ |
| AuthContext — Logout limpia estado | ✅ |
| AuthContext — hasRole verifica rol correctamente | ✅ |
| ThemeContext — Añade clase dark al documentElement | ✅ |
| ThemeContext — Renderiza children | ✅ |

#### Hooks (1 archivo, 5 tests)

| Test | Estado |
|---|---|
| useApi — Estado inicial loading | ✅ |
| useApi — Fetch exitoso | ✅ |
| useApi — Manejo de error en fetch | ✅ |
| useMutation — POST exitoso | ✅ |
| useMutation — Manejo de error en mutation | ✅ |

#### Components (7 archivos, 15 tests)

| Test | Estado |
|---|---|
| Breadcrumbs — No renderiza en ruta raíz | ✅ |
| Breadcrumbs — Renderiza links para ruta anidada | ✅ |
| Breadcrumbs — Renderiza múltiples segmentos | ✅ |
| DetailModal — No renderiza cuando está cerrado | ✅ |
| DetailModal — Renderiza contenido al abrir | ✅ |
| DetailModal — Llama onClose al hacer clic en overlay | ✅ |
| NotificationBell — Renderiza botón con contador | ✅ |
| Pagination — No renderiza con totalPages ≤ 1 | ✅ |
| Pagination — Renderiza números de página | ✅ |
| Pagination — Llama onPageChange al hacer clic | ✅ |
| Pagination — Deshabilita botón anterior en primera página | ✅ |
| ProtectedRoute — Redirige a login sin autenticación | ✅ |
| ProtectedRoute — Renderiza children autenticado | ✅ |
| ProtectedRoute — Muestra mensaje denied si rol no coincide | ✅ |
| SearchBar — Renderiza botón de búsqueda | ✅ |
| SplashScreen — Renderiza logo y nombre | ✅ |

#### Pages (7 archivos, 13 tests)

| Test | Estado |
|---|---|
| Dashboard — Muestra skeleton loading inicial | ✅ |
| Login — Renderiza splash screen inicial | ✅ |
| Login — Muestra selección de rol tras click | ✅ |
| Login — Muestra opciones de rol | ✅ |
| Profile — Renderiza información del perfil | ✅ |
| Profile — Renderiza formulario de edición | ✅ |
| Profile — Renderiza campos del formulario | ✅ |
| Productos — Renderiza página con título | ✅ |
| Productos — Muestra filtros por categoría | ✅ |
| Sucursales — Exporta componente Sucursales | ✅ |
| Tickets — Renderiza página con título | ✅ |
| Ventas — Muestra estado loading inicial | ✅ |
| Ventas — Renderiza página con título | ✅ |

---

## Módulo Backend (Java + JUnit 5 + JaCoCo)

### Configuración

- **Framework:** JUnit 5 + Mockito
- **Cobertura:** JaCoCo 0.8.12 con objetivo ≥ 60% por microservicio
- **Ejecución:** `mvn clean test` (requiere JDK 21+ y Maven 3.9+)

### Microservicios Backend

| Módulo | Tests | Instrucciones | Cobertura (Instrucciones) | Estado |
|---|---|---|---|---|---|
| ms-ventas | 86 | 3.852 | **91.1%** | ✅ |
| ms-datos-org | 22 | 1.396 | **99.2%** | ✅ |
| ms-indicadores | 38 | 737 | **62.9%** | ✅ |
| bff | 28 | 3.195 | **62.3%** | ✅ |
| api-gateway | 6 | 390 | **89.5%** | ✅ |
| **Total** | **180** | **9.570** | **—** | ✅ **100% ≥ 60%** |

### Cobertura por Paquete (ms-ventas) — Actualizada

| Paquete | Cobertura | Observación |
|---|---|---|
| builder (VentaBuilder) | 100% | Patrón Builder probado |
| entity | 100% | Entidades JPA |
| event (Observer) | 100% | Eventos de dominio |
| exception | 100% | Manejo global de errores |
| repository | 98% | Consultas personalizadas |
| service | **97%** | Añadidos tests para MindicadorService, ClimaService, AjustePrecioService |
| dto | 60% | DTOs restantes |
| controller | **96%** | Añadidos tests para ClimaController y EconomicoController |
| config | **97%** | Añadido DataInitializerTest con 6 casos |

### Tipos de Tests Backend

- **Unitarios:** Service, Builder, Factory, Strategy
- **Integración:** Repository con DataJpaTest
- **Controller:** MockMvc con WebMvcTest
- **Eventos:** Verificación de publicación y escucha de eventos

---

## Observaciones

1. **Pruebas frontend:** Ejecutan en entorno jsdom simulado. Los warnings de `act(...)` en pruebas de páginas con useEffect son esperados y no afectan los resultados.
2. **Canvas en Login.jsx:** El componente Background utiliza `requestAnimationFrame` con canvas. En jsdom esto genera warnings de `HTMLCanvasElement.getContext()` no implementado, lo cual es normal y no afecta las pruebas.
3. **Cobertura frontend:** Para obtener reporte de cobertura ejecutar `npm run test:coverage`.
