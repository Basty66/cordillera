package com.grupocordillera.datosorg.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "departamentos", schema = "datos_org")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}
