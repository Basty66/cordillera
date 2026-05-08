package com.grupocordillera.bff.dto;

import com.grupocordillera.bff.entity.Ticket;
import java.time.LocalDateTime;

public class TicketResponse {
    private Integer id;
    private String titulo;
    private String descripcion;
    private String status;
    private String prioridad;
    private String creadoPor;
    private String asignadoA;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TicketResponse fromEntity(Ticket t) {
        TicketResponse r = new TicketResponse();
        r.id = t.getId();
        r.titulo = t.getTitulo();
        r.descripcion = t.getDescripcion();
        r.status = t.getStatus().name();
        r.prioridad = t.getPrioridad().name();
        r.creadoPor = t.getCreadoPor();
        r.asignadoA = t.getAsignadoA();
        r.createdAt = t.getCreatedAt();
        r.updatedAt = t.getUpdatedAt();
        return r;
    }

    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getStatus() { return status; }
    public String getPrioridad() { return prioridad; }
    public String getCreadoPor() { return creadoPor; }
    public String getAsignadoA() { return asignadoA; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
