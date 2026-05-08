package com.grupocordillera.bff.repository;

import com.grupocordillera.bff.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByStatusOrderByCreatedAtDesc(Ticket.Status status);
    List<Ticket> findByCreadoPorOrderByCreatedAtDesc(String creadoPor);
    List<Ticket> findAllByOrderByCreatedAtDesc();
}
