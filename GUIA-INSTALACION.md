# Guía de Instalación — Plataforma de Monitoreo Grupo Cordillera

## Requisitos del Sistema

| Herramienta | Versión Mínima | Descripción |
|-------------|---------------|-------------|
| **Java** | 21+ | JDK para ejecutar los microservicios Spring Boot |
| **Node.js** | 20+ | Para el frontend React |
| **Maven** | 3.9+ | Build de los proyectos Java (o usar `mvnw.cmd`) |
| **PostgreSQL** | 14+ | Base de datos principal (o usar Neon.tech cloud) |
| **Git** | Cualquiera | Control de versiones |

---

## Dependencias Backend (Java / Maven)

Cada microservicio tiene su propio `pom.xml` con sus dependencias. El parent pom global (`pom.xml` raíz) agrupa todos los módulos.

### Todas las dependencias se descargan automáticamente con:

```bash
cd ms-ventas
.\mvnw.cmd clean install -DskipTests  # o mvn si está instalado globalmente
```

### Librerías principales del backend

| Librería | Versión | Propósito |
|----------|---------|-----------|
| **Spring Boot Starter Web** | 3.2.5 | API REST |
| **Spring Boot Starter Data JPA** | 3.2.5 | Persistencia con Hibernate |
| **Spring Boot Starter Validation** | 3.2.5 | Validación de DTOs |
| **Spring Cloud Gateway** | 4.1.2 | API Gateway y enrutamiento |
| **Spring Cloud Circuit Breaker (Resilience4j)** | 3.1.0 | Tolerancia a fallos |
| **Spring Boot Starter Security** | 3.2.5 | Autenticación y autorización |
| **Spring Boot Starter Actuator** | 3.2.5 | Monitoreo de salud |
| **PostgreSQL JDBC Driver** | 42.7.3 | Conexión a PostgreSQL |
| **H2 Database** | runtime | Base en memoria para BFF |
| **JJWT (api / impl / jackson)** | 0.12.6 | Generación y validación de tokens JWT |
| **Lombok** | opcional | Reducción de boilerplate |
| **SpringDoc OpenAPI** | 2.5.0 | Documentación Swagger |
| **JaCoCo** | 0.8.12 | Cobertura de pruebas (mínimo 60%) |

---

## Dependencias Frontend (React / Node.js)

Se instalan automáticamente con:

```bash
cd frontend
npm install
```

### Librerías principales del frontend

| Librería | Versión | Propósito |
|----------|---------|-----------|
| **React** | ^19.2.5 | Framework UI |
| **React DOM** | ^19.2.5 | Renderizado en navegador |
| **React Router DOM** | ^7.15.0 | Enrutamiento SPA |
| **Axios** | ^1.16.0 | Cliente HTTP para APIs |
| **Chart.js** | ^4.5.1 | Gráficos en dashboard |
| **react-chartjs-2** | ^5.3.1 | Wrapper React para Chart.js |
| **Recharts** | ^3.8.1 | Gráficos adicionales |
| **Framer Motion** | ^12.38.0 | Animaciones |
| **Lucide React** | ^1.14.0 | Iconos |
| **Tailwind CSS** | ^4.2.4 | Estilos utilitarios |
| **Vite** | ^8.0.10 | Build tool y dev server |
| **ESLint** | ^10.2.1 | Linter |

---

## Pasos para descargar y ejecutar

### 1. Clonar el repositorio

```bash
git clone https://github.com/Basty66/cordillera.git
cd cordillera
```

### 2. Iniciar microservicios backend (orden sugerido)

Abrir **5 terminales** (una por cada servicio):

```bash
# Terminal 1 — Microservicio de Ventas (puerto 8081)
cd ms-ventas
.\mvnw.cmd spring-boot:run -q

# Terminal 2 — Microservicio Datos Org (puerto 8082)
cd ms-datos-org
.\mvnw.cmd spring-boot:run -q

# Terminal 3 — Microservicio Indicadores (puerto 8083)
cd ms-indicadores
.\mvnw.cmd spring-boot:run -q

# Terminal 4 — BFF + Auth (puerto 8090)
cd bff
.\mvnw.cmd spring-boot:run -q

# Terminal 5 — API Gateway (puerto 8084)
cd api-gateway
.\mvnw.cmd spring-boot:run -q
```

Los microservicios se conectan automáticamente a la base de datos PostgreSQL en Neon.tech (nube). No necesitas instalar PostgreSQL localmente.

### 3. Iniciar frontend

```bash
cd frontend
npm install
npm run dev
```

El frontend se abrirá en `http://localhost:5173`.

---

## Credenciales de prueba

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `admin123` | ADMIN |
| `vendedor` | `ventas123` | VENDEDOR |
| `bodega` | `bodega123` | BODEGA |
| `carla` | `carla123` | VENDEDOR |
| `pedro` | `pedro123` | BODEGA |
| `ana` | `ana123` | ADMIN |
| `luis` | `luis123` | VENDEDOR |

---

## Notas importantes

- Los archivos `mvnw.cmd` (Maven Wrapper) ya están incluidos en cada módulo, **no necesitas tener Maven instalado globalmente**.
- La base de datos PostgreSQL es **remota** (Neon.tech), no requiere configuración local.
- El BFF usa **H2 en memoria** para usuarios y tickets, no necesita PostgreSQL.
- Si usas Linux/Mac, usa `./mvnw` en lugar de `.\mvnw.cmd`.
- Si Maven da error de memoria, puedes usar `set MAVEN_OPTS=-Xmx512m` en Windows o `export MAVEN_OPTS="-Xmx512m"` en Linux/Mac.
