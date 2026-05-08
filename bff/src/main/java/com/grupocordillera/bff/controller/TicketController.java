package com.grupocordillera.bff.controller;

import com.grupocordillera.bff.dto.TicketRequest;
import com.grupocordillera.bff.dto.TicketResponse;
import com.grupocordillera.bff.entity.Ticket;
import com.grupocordillera.bff.repository.TicketRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public List<TicketResponse> listar() {
        return ticketRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(TicketResponse::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> obtener(@PathVariable Integer id) {
        return ticketRepository.findById(id)
                .map(t -> ResponseEntity.ok(TicketResponse.fromEntity(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TicketResponse crear(@RequestBody TicketRequest req) {
        Ticket t = new Ticket(req.getTitulo(), req.getDescripcion(), req.getPrioridad(), req.getCreadoPor());
        t.setAsignadoA(req.getAsignadoA());
        return TicketResponse.fromEntity(ticketRepository.save(t));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketResponse> actualizarStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return ticketRepository.findById(id).map(t -> {
            t.setStatus(Ticket.Status.valueOf(body.get("status")));
            return ResponseEntity.ok(TicketResponse.fromEntity(ticketRepository.save(t)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
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
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
