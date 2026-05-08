package com.grupocordillera.ms_ventas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "detalle_ventas", schema = "ventas")
// Evita que Swagger/Jackson explote al intentar leer los proxies de Hibernate (Lazy Loading)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación con la cabecera
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonIgnore // Evita bucle infinito en el JSON y errores en Swagger
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /**
     * CRUCIAL: 'insertable = false, updatable = false'
     * Esto soluciona el error: "subtotal is a generated column".
     * Le dice a Hibernate: "Solo lee este valor, deja que la DB lo calcule".
     */
    @Column(name = "subtotal", precision = 10, scale = 2, insertable = false, updatable = false)
    private BigDecimal subtotal;
}