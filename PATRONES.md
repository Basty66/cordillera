# Patrones de Diseño Implementados

## Observer Pattern
- VentaRegistradaEvent.java - Evento publicado al registrar venta
- VentaEventListener.java - Escucha y loggea eventos de venta
- StockUpdateListener.java - Verifica stock post-venta

## Builder Pattern
- VentaBuilder.java - Construcción paso a paso de objetos Venta

## Repository Pattern
- Interfaces JpaRepository en todos los módulos

## Factory Method Pattern
- EmpleadoFactory (ms-datos-org)
- CalculoIndicadorFactory (ms-indicadores)

## Strategy Pattern
- CalculoStrategy y 3 implementaciones (Ventas, Inventario, Rentabilidad)

## Circuit Breaker Pattern
- Resilience4j en api-gateway y bff

## BFF Pattern
- Backend For Frontend en módulo bff/

## API Gateway Pattern
- Punto único de entrada en api-gateway/
