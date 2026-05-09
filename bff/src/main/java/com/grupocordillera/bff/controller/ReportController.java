package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.TicketResponse;
import com.grupocordillera.bff.entity.Ticket;
import com.grupocordillera.bff.repository.TicketRepository;
import com.grupocordillera.bff.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes BFF", description = "Reportes del sistema (tickets y usuarios)")
public class ReportController {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;

    public ReportController(TicketRepository ticketRepository, UsuarioRepository usuarioRepository) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard de reportes", description = "Retorna estadisticas de tickets y usuarios del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte obtenido exitosamente")
    })
    public ResponseEntity<Map<String, Object>> dashboardReport() {
        List<Ticket> todos = ticketRepository.findAll();
        Map<String, Object> report = new LinkedHashMap<>();

        report.put("totalTickets", todos.size());
        report.put("ticketsAbiertos", todos.stream().filter(t -> t.getStatus() == Ticket.Status.ABIERTO).count());
        report.put("ticketsEnProgreso", todos.stream().filter(t -> t.getStatus() == Ticket.Status.EN_PROGRESO).count());
        report.put("ticketsResueltos", todos.stream().filter(t -> t.getStatus() == Ticket.Status.RESUELTO).count());
        report.put("ticketsCerrados", todos.stream().filter(t -> t.getStatus() == Ticket.Status.CERRADO).count());
        report.put("ticketsCriticos", todos.stream().filter(t -> t.getPrioridad() == Ticket.Prioridad.CRITICA && t.getStatus() != Ticket.Status.CERRADO).count());
        report.put("totalUsuarios", usuarioRepository.count());

        Map<String, Long> porPrioridad = new LinkedHashMap<>();
        for (Ticket.Prioridad p : Ticket.Prioridad.values()) {
            porPrioridad.put(p.name(), todos.stream().filter(t -> t.getPrioridad() == p).count());
        }
        report.put("porPrioridad", porPrioridad);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/tickets")
    @Operation(summary = "Exportar tickets", description = "Retorna todos los tickets para exportacion")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tickets exportados exitosamente")
    })
    public ResponseEntity<List<TicketResponse>> exportTickets() {
        return ResponseEntity.ok(
            ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(TicketResponse::fromEntity).toList()
        );
    }
}
