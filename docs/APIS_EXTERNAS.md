# APIs Externas — Grupo Cordillera

## 1. OpenWeatherMap — Clima en Sucursales

**Endpoint:** `api.openweathermap.org/data/2.5/weather`

### ¿Qué obtuvimos?

Clima en tiempo real para 9 ciudades donde operan sucursales o centros de distribución: Santiago, Valparaíso, Concepción, La Serena, Antofagasta, Temuco, Rancagua, Iquique y Puerto Montt.

### ¿Cómo lo consumimos?

| Capa | Archivo | Rol |
|------|---------|-----|
| Backend | `ClimaService.java` | Orquesta llamada a OpenWeatherMap y mapea respuesta a `ClimaDTO` |
| Frontend | `Dashboard.jsx` | Renderiza temperatura, humedad, ícono y descripción por ciudad |

**Flujo:** Frontend (`GET /clima/sucursales`) → Backend Controller → `ClimaService.obtenerClimaPorCiudad()` → OpenWeatherMap API

### ¿Qué hicimos para dejarla funcionando?

- **API key opcional** configurable via `openweather.api.key` en `application.yml`. Si no se provee o es el placeholder, el sistema **no se cae** — genera datos simulados determinísticos basados en el hash de cada ciudad.
- **HttpClient nativo** (`java.net.http`) con timeout de 5s conexión + 8s lectura.
- **Cache** con Spring `@Cacheable("clima")` para evitar llamar repetido por cada request.
- **Simulación incorporada**: las 9 ciudades obtienen datos ficticios pero coherentes (Santiago ~28°C, Concepción ~20°C, etc.).
- **Marcación explícita**: el código lleva el comentario `/** EXTERNAL API — OpenWeatherMap */` y el frontend muestra un badge "API Externa" en la card de clima.

### ¿En qué ayuda al negocio?

- Logística y distribución: saber condiciones climáticas en cada sucursal permite planificar despachos, evitar retrasos por lluvia y proteger mercadería sensible.
- Atención al cliente: el equipo de soporte puede anticipar problemas de acceso a sucursales en zonas con mal clima.
- Dashboard unificado: los datos climáticos se muestran junto a indicadores de ventas, dando contexto estacional a los números.

---

## 2. mindicador.cl — Indicadores Económicos

**Endpoint:** `mindicador.cl/api`

### ¿Qué obtuvimos?

Valores diarios de los principales indicadores económicos chilenos:

| Indicador | Clave | Unidad | Ejemplo |
|-----------|-------|--------|---------|
| Unidad de Fomento | UF | Pesos | $36.146,11 |
| Dólar Observado | DOLAR | Pesos | $920,50 |
| Unidad Tributaria Mensual | UTM | Pesos | $63.274 |
| Índice de Precios al Consumidor | IPC | Porcentaje | -0,5% |
| Euro | EURO | Pesos | $1.020 |

### ¿Cómo lo consumimos?

| Capa | Archivo | Rol |
|------|---------|-----|
| Backend | `MindicadorService.java` | Llama a mindicador.cl, parsea JSON con Jackson y devuelve `IndicadorEconomicoDTO` |
| Frontend | `Dashboard.jsx` | Muestra UF, Dólar, UTM, IPC en cards compactas con formato CLP |

**Flujo:** Frontend (`GET /economico/indicadores`) → Backend Controller → `MindicadorService.obtenerIndicadores()` → mindicador.cl API

### ¿Qué hicimos para dejarla funcionando?

- **Sin API key** — mindicador.cl es un endpoint público gratuito.
- **HttpClient nativo** con timeout de 10s.
- **Cache** con Spring `@Cacheable("indicadoresEconomicos")` y método `limpiarCache()` con `@CacheEvict` para refresco manual.
- **Fallback robusto**: si la API no responde o hay error de red, retorna valores predefinidos con fecha "Simulado", que el frontend muestra con un badge informativo.
- **Helper `obtenerUfActual()`** expone solo la UF para cálculos internos (ej. contratos, cotizaciones).

### ¿En qué ayuda al negocio?

- **Cotizaciones en UF**: los contratos y cotizaciones del negocio se expresan frecuentemente en UF — tener el valor actualizado evita cálculos manuales y errores.
- **Análisis financiero**: el IPC ayuda a contextualizar si los aumentos de ventas son reales o solo inflación. El Dólar es útil para importaciones y costos de insumos.
- **Toma de decisiones**: los valores se muestran junto a KPIs de ventas en el Dashboard, permitiendo correlacionar rendimiento comercial con contexto macroeconómico.
- **Automatización**: al estar integrado, cualquier módulo del sistema (reportes, cotizaciones, proyecciones) puede consumir estos valores sin intervención humana.

---

## Resumen Técnico

| Aspecto | OpenWeatherMap | mindicador.cl |
|---------|----------------|---------------|
| Tipo | API Key (gratuita) | Pública sin key |
| Formato | JSON | JSON |
| Backend | `ClimaService.java` | `MindicadorService.java` |
| Cache | `@Cacheable("clima")` | `@Cacheable("indicadoresEconomicos")` |
| Fallback | Datos simulados por hash | Valores hardcodeados |
| Tiempo de vida del dato | Por request (caché de sesión) | Diario (configurable) |
| Frontend | Dashboard (cards por ciudad) | Dashboard (cards UF/Dólar/UTM/IPC) |

## Prerrequisitos

```yaml
# Solo para OpenWeatherMap (opcional)
openweather:
  api:
    key: TU_API_KEY_AQUI
```

Sin este valor el sistema funciona igual con datos simulados. Para obtener una API key gratuita: https://openweathermap.org/appid
