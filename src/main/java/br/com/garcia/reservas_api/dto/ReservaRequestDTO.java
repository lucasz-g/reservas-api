package br.com.garcia.reservas_api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReservaRequestDTO(
    @NotNull(message = "A sala é obrigatória")
    Long salaId,

    @NotBlank(message = "O nome do solicitante é obrigatório")
    String nomeSolicitante,

    @NotBlank(message = "O e-mail do solicitante é obrigatório")
    String email,

    @NotNull(message = "A data da reserva é obrigatória")
    LocalDate dataReserva,

    @NotNull(message = "A hora de início da reserva é obrigatória")
    LocalTime horaInicio,

    @NotNull(message = "A hora de fim da reserva é obrigatória")
    LocalTime horaFim,

    @NotBlank(message = "A finalidade da reserva é obrigatória")
    @Size(max = 255, message = "A finalidade deve ter no máximo 255 caracteres")
    String finalidade
) {
    // Validação: horário final deve ser maior que o inicial
    @AssertTrue(message = "A hora de fim deve ser depois da hora de início")
    public boolean isHorarioValido() {
        return horaInicio == null || horaFim == null || horaInicio.isBefore(horaFim);
    }
}
