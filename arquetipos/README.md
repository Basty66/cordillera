# Arquetipos Maven - Grupo Cordillera

Esta carpeta contiene los arquetipos Maven personalizados utilizados como base para los componentes backend del proyecto.

Los arquetipos se encuentran en la carpeta `archetypes/` (inglés). Esta carpeta es una referencia en español para facilitar la navegación.

## Arquetipos Disponibles

### ms-service-archetype
Arquetipo para microservicios con Spring Boot 3.2 + JPA + PostgreSQL + Validation.
Ubicación: `archetypes/ms-service-archetype/`

### bff-archetype
Arquetipo para módulos BFF con Spring Boot 3.2 + Security + JWT + Resilience4j + H2.
Ubicación: `archetypes/bff-archetype/`

## Cómo Usar los Arquetipos

```bash
# Generar un nuevo microservicio
mvn archetype:generate \
  -DarchetypeGroupId=com.grupocordillera \
  -DarchetypeArtifactId=ms-service-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.grupocordillera \
  -DartifactId=ms-nuevo-servicio \
  -Dversion=1.0.0

# Generar un nuevo BFF
mvn archetype:generate \
  -DarchetypeGroupId=com.grupocordillera \
  -DarchetypeArtifactId=bff-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.grupocordillera \
  -DartifactId=nuevo-bff \
  -Dversion=1.0.0
```

## Instalación Local

```bash
cd archetypes/ms-service-archetype
mvn clean install

cd ../bff-archetype
mvn clean install
```
