package com.grupocordillera.indicadores.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "valores_indicador", schema = "indicadores")
public class ValorIndicador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicador_id", nullable = false)
    private Indicador indicador;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal valor;

    @Column(name = "periodo", length = 7)
    private String periodo;

    @Column(name = "fecha_calculo")
    private LocalDate fechaCalculo;
}
