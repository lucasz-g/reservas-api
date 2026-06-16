package br.com.garcia.reservas_api.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.garcia.reservas_api.exceptions.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "reservas")
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "A sala é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @NotBlank(message = "O nome do solicitante é obrigatório")
    @Column(name = "nome_solicitante", nullable = false)
    private String nomeSolicitante;

    @NotBlank(message = "O e-mail do solicitante é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull(message = "A data da reserva é obrigatória")
    @Column(name = "data_reserva", nullable = false)
    private LocalDate dataReserva;

    @NotNull(message = "A hora de início da reserva é obrigatória")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull(message = "A hora de fim da reserva é obrigatória")
    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @NotBlank(message = "A finalidade da reserva é obrigatória")
    @Column(name = "finalidade", nullable = false)
    private String finalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusReserva status = StatusReserva.ATIVA;

    // Métodos de negócio
    public void cancelar() {
        if (status == StatusReserva.CANCELADA) {
            throw new RegraNegocioException("Reserva ja esta cancelada");
        }
        this.status = StatusReserva.CANCELADA;
    }
}
