package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.TicketRequest;
import com.grupocordillera.bff.dto.TicketResponse;
import com.grupocordillera.bff.entity.Ticket;
import com.grupocordillera.bff.repository.TicketRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Operaciones de tickets de soporte")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    @Operation(summary = "Listar tickets", description = "Retorna una lista de todos los tickets ordenados por fecha de creacion descendente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida exitosamente")
    })
    public List<TicketResponse> listar() {
        return ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(TicketResponse::fromEntity).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ticket", description = "Retorna un ticket especifico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
        @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    public ResponseEntity<TicketResponse> obtener(@PathVariable Integer id) {
        return ticketRepository.findById(id)
                .map(t -> ResponseEntity.ok(TicketResponse.fromEntity(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear ticket", description = "Crea un nuevo ticket de soporte")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public TicketResponse crear(@RequestBody TicketRequest req) {
        Ticket t = new Ticket(req.getTitulo(), req.getDescripcion(), req.getPrioridad(), req.getCreadoPor());
        t.setAsignadoA(req.getAsignadoA());
        return TicketResponse.fromEntity(ticketRepository.save(t));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado", description = "Actualiza el estado de un ticket existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    public ResponseEntity<TicketResponse> actualizarStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return ticketRepository.findById(id).map(t -> {
            t.setStatus(Ticket.Status.valueOf(body.get("status")));
            return ResponseEntity.ok(TicketResponse.fromEntity(ticketRepository.save(t)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ticket", description = "Actualiza los campos de un ticket existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ticket actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    public ResponseEntity<TicketResponse> actualizar(@PathVariable Integer id, @RequestBody TicketRequest req) {
        return ticketRepository.findById(id).map(t -> {
            if (req.getTitulo() != null) t.setTitulo(req.getTitulo());
            if (req.getDescripcion() != null) t.setDescripcion(req.getDescripcion());
            if (req.getPrioridad() != null) t.setPrioridad(req.getPrioridad());
            if (req.getAsignadoA() != null) t.setAsignadoA(req.getAsignadoA());
            return ResponseEntity.ok(TicketResponse.fromEntity(ticketRepository.save(t)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ticket", description = "Elimina un ticket del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ticket eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
