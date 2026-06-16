package com.grupocordillera.bff.dto;

import java.util.List;
import java.util.Map;

public record TicketAnalyticsDTO(
    long totalTickets,
    long abiertos,
    long enProgreso,
    long resueltos,
    long cerrados,
    long criticosAbiertos,
    double tiempoPromedioResolucionHoras,
    Map<String, Long> porPrioridad,
    Map<String, Long> porCategoria,
    List<TicketTrendDTO> tendenciaUltimos7Dias,
    Map<String, Long> ticketsPorUsuario
) {}
