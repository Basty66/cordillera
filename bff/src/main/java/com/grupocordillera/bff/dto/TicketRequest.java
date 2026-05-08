package com.grupocordillera.bff.dto;

import com.grupocordillera.bff.entity.Ticket;

public class TicketRequest {
    private String titulo;
    private String descripcion;
    private Ticket.Prioridad prioridad;
    private String creadoPor;
    private String asignadoA;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Ticket.Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Ticket.Prioridad prioridad) { this.prioridad = prioridad; }
    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
    public String getAsignadoA() { return asignadoA; }
    public void setAsignadoA(String asignadoA) { this.asignadoA = asignadoA; }
}
