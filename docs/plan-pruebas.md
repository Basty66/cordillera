# Plan de Pruebas — Plataforma de Monitoreo Grupo Cordillera

## 1. Objetivo
Garantizar la calidad del software mediante pruebas unitarias e integración en los 5 módulos del backend (ms-ventas, ms-datos-org, ms-indicadores, bff, api-gateway), cubriendo patrones de diseño, servicios, controladores, seguridad y tolerancia a fallos.

## 2. Alcance

### Módulos bajo prueba
| Módulo | Puerto | Pruebas unitarias | Pruebas integración |
|--------|--------|:-:|:-:|
| ms-ventas | 8081 | 12 | 1 |
| ms-datos-org | 8082 | 6 | 1 |
| ms-indicadores | 8083 | 7 | 1 |
| bff | 8090 | 6 | 2 |
| api-gateway | 8084 | 2 | 1 |
| **Total** | | **33** | **6** |

### Framework
- **JUnit 5** (`@Test`, `@ExtendWith`)
- **Mockito** (`@Mock`, `@InjectMocks`, `when`, `verify`)
- **MockMvc** (controladores Web MVC)
- **WebTestClient** (controladores WebFlux)
- **JaCoCo** (cobertura mínima 60%)

## 3. Estrategia por capa

### 3.1 Entidades y Builders
- Construcción de objetos con VentaBuilder
- Validación de campos requeridos
- Cálculo de totales

### 3.2 Repositorios (lógica personalizada)
- Consultas JPQL nativas (VentaRepositoryImpl)
- Stored procedures
- Manejo de `EntityManager`

### 3.3 Servicios
- Lógica de negocio con mocks de repositorios
- Registro de ventas, validación de stock
- Generación masiva de datos
- Manejo de fallos (Circuit Breaker fallbacks)

### 3.4 Patrones de diseño
| Patrón | Prueba |
|--------|--------|
| **Builder** | VentaBuilder: construcción completa, errores |
| **Factory Method** | EmpleadoFactory, CalculoIndicadorFactory |
| **Strategy** | CalculoVentasStrategy, CalculoInventarioStrategy, CalculoRentabilidadStrategy |
| **Observer** | VentaEventListener, StockUpdateListener |
| **Circuit Breaker** | VentaClient, DatosOrgClient, IndicadorClient (fallbacks) |

### 3.5 Controladores
- `MockMvc` standalone con servicios mock
- Status HTTP, JSON response structure
- Validación de parámetros

### 3.6 Seguridad (JWT)
- Generación y validación de tokens
- Extracción de claims (username, rol)
- Middleware de autenticación

### 3.7 Gateway
- Endpoints de fallback (503)
- Health check

## 4. Casos de prueba clave

### ms-ventas
| # | Caso | Tipo |
|---|------|------|
| V1 | Registrar venta exitosa | Unitario |
| V2 | Registrar venta sin detalles → error | Unitario |
| V3 | Registrar venta con stock insuficiente → error | Unitario |
| V4 | VentaBuilder construye venta correctamente | Unitario |
| V5 | VentaBuilder sin sucursal → error | Unitario |
| V6 | EventListener no lanza excepciones | Unitario |
| V7 | StockUpdateListener procesa evento sin errores | Unitario |

### ms-datos-org
| # | Caso | Tipo |
|---|------|------|
| O1 | EmpleadoFactory crea empleado individual | Unitario |
| O2 | EmpleadoFactory crea empleados masivos | Unitario |
| O3 | Servicio genera empleados masivos | Unitario |
| O4 | Controlador lista empleados | Unitario |

### ms-indicadores
| # | Caso | Tipo |
|---|------|------|
| I1 | Factory crea strategy VENTAS | Unitario |
| I2 | Factory crea strategy INVENTARIO | Unitario |
| I3 | Factory crea strategy RENTABILIDAD | Unitario |
| I4 | Factory lanza error para tipo no soportado | Unitario |
| I5 | Cálculo VENTAS con valores normales | Unitario |
| I6 | Cálculo VENTAS con cero transacciones | Unitario |
| I7 | Cálculo INVENTARIO rotación | Unitario |
| I8 | Cálculo INVENTARIO inventario cero | Unitario |
| I9 | Cálculo RENTABILIDAD margen | Unitario |
| I10 | Cálculo RENTABILIDAD ingresos cero | Unitario |

### bff
| # | Caso | Tipo |
|---|------|------|
| B1 | Login exitoso devuelve JWT | Integración |
| B2 | Login con credenciales incorrectas → 401 | Integración |
| B3 | Acceso sin token → 403 | Integración |
| B4 | Dashboard con datos reales | Unitario |
| B5 | Dashboard con fallbacks (valores cero) | Unitario |
| B6 | JWT genera y valida token correctamente | Unitario |
| B7 | JWT extrae username y rol del token | Unitario |
| B8 | AuthController login falla con usuario inactivo | Unitario |
| B9 | ReportController dashboard report | Unitario |

### api-gateway
| # | Caso | Tipo |
|---|------|------|
| G1 | Health endpoint retorna UP | Unitario |
| G2 | Fallback ventas retorna 503 | Unitario |
| G3 | Fallback indicadores retorna 503 | Unitario |

## 5. Cobertura esperada
- Líneas de código: **≥ 60%** por módulo (validado con JaCoCo)
- Clases críticas con cobertura ≥ 80%:
  - VentaService, ProductoService, SucursalService
  - EmpleadoService, IndicadorService
  - DashboardService, JwtUtil
  - Todas las estrategias (Strategy Pattern)
  - Todos los factories (Factory Method Pattern)
  - Todos los listeners (Observer Pattern)
  - Controladores REST principales

## 6. Ejecución
```bash
# Todos los módulos
cd ms-ventas && .\mvnw.cmd test
cd ms-datos-org && .\mvnw.cmd test
cd ms-indicadores && .\mvnw.cmd test
cd bff && .\mvnw.cmd test
cd api-gateway && .\mvnw.cmd test
```
