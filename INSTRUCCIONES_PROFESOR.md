# INSTRUCCIONES PARA EJECUTAR EL PROYECTO
## Grupo Cordillera — Plataforma de Monitoreo Inteligente

---

## Requisitos

- **Docker Desktop** instalado y funcionando
  - Descargar desde: https://www.docker.com/products/docker-desktop/
  - Windows: ejecutar Docker Desktop y esperar a que aparezca "Engine running"
- ~3 GB de espacio libre en disco
- **Conexion a internet** para descargar las imagenes (solo la primera vez)

---

## Paso 1: Descargar el archivo de configuracion

Descargar el archivo `docker-compose.yml` desde:
> https://raw.githubusercontent.com/Basty66/cordillera/master/docker-compose.yml

O copiar manualmente el contenido en un archivo llamado `docker-compose.yml`.

---

## Paso 2: Ejecutar el sistema

Abrir una terminal (CMD o PowerShell) en la misma carpeta donde guardaste el archivo y ejecutar:

```bash
docker compose up -d
```

Docker descargara automaticamente las 6 imagenes y las iniciara.

**Salida esperada (primera vez):**
```
[+] Pulling images (6/6) done
[+] Running 7/7
 ✔ Container pg-cordillera       Started
 ✔ Container ms-ventas           Started
 ✔ Container ms-datos-org        Started
 ✔ Container ms-indicadores      Started
 ✔ Container bff                 Started
 ✔ Container api-gateway         Started
 ✔ Container frontend            Started
```

---

## Paso 3: Esperar a que carguen los datos

Los microservicios crean automaticamente las tablas y los datos de prueba al iniciar. Este proceso toma aproximadamente **2-3 minutos**.

Para verificar que todo esta listo, revisar los logs:

```bash
docker compose logs ms-ventas --tail 20
```

Cuando veas mensajes como:
```
CARGA COMPLETADA en Xs
```
...significa que los datos estan listos.

---

## Paso 4: Acceder al sistema

Abrir el navegador web en:

> **http://localhost:5173**

---

## Paso 5: Iniciar sesion

Usar las siguientes credenciales de prueba:

| Usuario   | Contrasena | Rol      |
|-----------|-----------|----------|
| `admin`   | `admin123` | ADMIN   |
| `vendedor`| `ventas123`| VENDEDOR |
| `bodega`  | `bodega123`| BODEGA   |

---

## Comandos utiles

| Accion | Comando |
|--------|---------|
| Ver todos los servicios en ejecucion | `docker compose ps` |
| Ver logs de un servicio | `docker compose logs ms-ventas --tail 50` |
| Ver logs de todos los servicios | `docker compose logs --tail 20` |
| Detener el sistema | `docker compose down` |
| Detener y borrar datos | `docker compose down -v` |
| Ver uso de recursos | `docker stats` |
| Actualizar imagenes | `docker compose pull` |

---

## Arquitectura del sistema

```
Navegador (http://localhost:5173)
        │
        ▼
   Frontend (React 19)
        │
        ▼
   API Gateway (puerto 8084)
        │
        ├── ms-ventas (puerto 8081) → Ventas, Productos, Sucursales
        ├── ms-datos-org (puerto 8082) → Empleados, Departamentos
        ├── ms-indicadores (puerto 8083) → KPIs, Indicadores
        └── BFF (puerto 8090) → Auth, Dashboard, Tickets, Reportes
                │
                ▼
   PostgreSQL (puerto 5432) — Base de datos local
```

---

## Solucion de problemas

**Error: "port is already allocated"**
→ Ejecutar `net stop winnat` en PowerShell como Administrador, luego reintentar.

**Error: "Cannot connect to the Docker daemon"**
→ Abrir Docker Desktop y esperar a que inicie.

**La pagina no carga en localhost:5173**
→ Ejecutar `docker compose ps` para verificar que todos los servicios esten "Up".
→ Si alguno no esta iniciado, ejecutar `docker compose logs <nombre-servicio>` para ver el error.

**Error en el login**
→ Esperar 1-2 minutos mas a que terminen de cargar los datos.
→ Revisar logs: `docker compose logs bff --tail 20`

---

## Endpoints de la API (para pruebas con Postman)

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| POST | `http://localhost:8084/api/auth/login` | Iniciar sesion |
| GET | `http://localhost:8084/api/bff/dashboard` | Dashboard consolidado |
| GET | `http://localhost:8084/api/ventas?pagina=0&tamano=20` | Ventas paginadas |
| GET | `http://localhost:8084/api/productos` | Listar productos |
| GET | `http://localhost:8084/api/empleados` | Listar empleados |
| GET | `http://localhost:8084/api/indicadores/valores/actuales` | KPIs actuales |
| GET | `http://localhost:8084/health` | Health check del API Gateway |
| GET | `http://localhost:8084/api/auth/health` | Health check del BFF |

Documentacion Swagger disponible en:
- http://localhost:8081/swagger-ui.html (ms-ventas)
- http://localhost:8082/swagger-ui.html (ms-datos-org)
- http://localhost:8083/swagger-ui.html (ms-indicadores)

---

## Tecnologias utilizadas

- **Backend**: Java 21, Spring Boot 3.2.5, Spring Data JPA, Hibernate
- **Frontend**: React 19, Vite 8, Tailwind CSS v4, Axios, Recharts
- **Base de datos**: PostgreSQL 14 (local en Docker)
- **Arquitectura**: Microservicios, API Gateway, BFF, Circuit Breaker (Resilience4j)
- **Autenticacion**: JWT (jjwt 0.12.6)
- **Patrones de diseno**: Repository, Factory Method, Strategy, Observer, Builder, BFF, API Gateway, Custom Hook
