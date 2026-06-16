package br.com.garcia.reservas_api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponseDTO(
    Long id,
    String nomeSolicitante,
    String email,
    LocalDate dataReserva,
    LocalTime horaInicio,
    LocalTime horaFim,
    String finalidade
) {
    
}
