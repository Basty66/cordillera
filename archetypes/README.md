# Maven Archetypes - Grupo Cordillera

This directory contains Maven archetypes for generating backend components.

## Available Archetypes

### ms-service-archetype
Archetype for microservices with Spring Boot 3.2 + JPA + PostgreSQL + Validation.

**Generate a new microservice:**
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.grupocordillera \
  -DarchetypeArtifactId=ms-service-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.grupocordillera \
  -DartifactId=ms-nuevo-servicio \
  -Dversion=1.0.0
```

### bff-archetype
Archetype for BFF modules with Spring Boot 3.2 + Security + JWT + Resilience4j + H2.

**Generate a new BFF:**
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.grupocordillera \
  -DarchetypeArtifactId=bff-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=com.grupocordillera \
  -DartifactId=nuevo-bff \
  -Dversion=1.0.0
```

## Install Archetypes Locally

From the project root:
```bash
cd archetypes/ms-service-archetype
mvn clean install

cd ../bff-archetype
mvn clean install
```

## Structure

Each archetype follows the standard Maven archetype layout:
```
archetype/
├── pom.xml
└── src/main/resources/
    ├── META-INF/maven/archetype-metadata.xml
    └── archetype-resources/
        ├── pom.xml
        └── src/main/java/__packageInPathFormat__/
```

The `__packageInPathFormat__` is replaced with the actual package path during generation.
