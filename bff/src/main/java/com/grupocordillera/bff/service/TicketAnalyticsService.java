package com.grupocordillera.bff.service;

import com.grupocordillera.bff.dto.TicketAnalyticsDTO;
import com.grupocordillera.bff.dto.TicketTrendDTO;
import com.grupocordillera.bff.entity.Ticket;
import com.grupocordillera.bff.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketAnalyticsService {

    private final TicketRepository ticketRepository;
    private final TicketClassificationService classificationService;

    public TicketAnalyticsService(TicketRepository ticketRepository,
                                  TicketClassificationService classificationService) {
        this.ticketRepository = ticketRepository;
        this.classificationService = classificationService;
    }

    public TicketAnalyticsDTO obtenerAnalytics() {
        List<Ticket> todos = ticketRepository.findAll();
        if (todos.isEmpty()) {
            return new TicketAnalyticsDTO(0, 0, 0, 0, 0, 0, 0, Map.of(), Map.of(), List.of(), Map.of());
        }

        long abiertos = todos.stream().filter(t -> t.getStatus() == Ticket.Status.ABIERTO).count();
        long enProgreso = todos.stream().filter(t -> t.getStatus() == Ticket.Status.EN_PROGRESO).count();
        long resueltos = todos.stream().filter(t -> t.getStatus() == Ticket.Status.RESUELTO).count();
        long cerrados = todos.stream().filter(t -> t.getStatus() == Ticket.Status.CERRADO).count();
        long criticosAbiertos = todos.stream()
                .filter(t -> t.getPrioridad() == Ticket.Prioridad.CRITICA && t.getStatus() != Ticket.Status.CERRADO)
                .count();

        double tiempoPromedio = calcularTiempoPromedioResolucion(todos);

        Map<String, Long> porPrioridad = Arrays.stream(Ticket.Prioridad.values())
                .collect(Collectors.toMap(Enum::name, p -> todos.stream().filter(t -> t.getPrioridad() == p).count()));

        Map<String, Long> porCategoria = todos.stream()
                .collect(Collectors.groupingBy(
                    t -> classificationService.clasificar(t.getTitulo(), t.getDescripcion()),
                    Collectors.counting()
                ));

        List<TicketTrendDTO> tendencia = calcularTendencia7Dias(todos);

        Map<String, Long> porUsuario = todos.stream()
                .filter(t -> t.getCreadoPor() != null)
                .collect(Collectors.groupingBy(Ticket::getCreadoPor, Collectors.counting()));

        return new TicketAnalyticsDTO(
            todos.size(), abiertos, enProgreso, resueltos, cerrados,
            criticosAbiertos, tiempoPromedio, porPrioridad, porCategoria,
            tendencia, porUsuario
        );
    }

    private double calcularTiempoPromedioResolucion(List<Ticket> tickets) {
        var resueltos = tickets.stream()
                .filter(t -> (t.getStatus() == Ticket.Status.RESUELTO || t.getStatus() == Ticket.Status.CERRADO)
                        && t.getCreatedAt() != null && t.getUpdatedAt() != null)
                .toList();

        if (resueltos.isEmpty()) return 0;

        return resueltos.stream()
                .mapToLong(t -> ChronoUnit.HOURS.between(t.getCreatedAt(), t.getUpdatedAt()))
                .average()
                .orElse(0);
    }

    private List<TicketTrendDTO> calcularTendencia7Dias(List<Ticket> tickets) {
        List<TicketTrendDTO> trend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate dia = LocalDate.now().minusDays(i);
            long creados = tickets.stream()
                    .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().toLocalDate().equals(dia))
                    .count();
            long resueltos = tickets.stream()
                    .filter(t -> (t.getStatus() == Ticket.Status.RESUELTO || t.getStatus() == Ticket.Status.CERRADO)
                            && t.getUpdatedAt() != null && t.getUpdatedAt().toLocalDate().equals(dia))
                    .count();
            trend.add(new TicketTrendDTO(dia.toString(), creados, resueltos));
        }
        return trend;
    }
}
