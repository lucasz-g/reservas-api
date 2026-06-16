package br.com.garcia.reservas_api.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "salas")
@AllArgsConstructor
@NoArgsConstructor
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da sala é obrigatório")
    @Column(nullable = false, unique = true, name = "nome")
    private String nome;

    @NotNull(message = "A capacidade da sala é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser maior que zero")
    @Column(name = "capacidade", nullable = false)
    private Integer capacidade;

    @NotBlank(message = "A localização da sala é obrigatória")
    @Size(max = 100, message = "A localização deve ter no máximo 100 caracteres")
    @Column(name = "localizacao", nullable = false)
    private String localizacao;

    @NotNull(message = "O status da sala é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusSala status = StatusSala.ATIVA;

    // Validação 
    public void validarReserva() {
        if (status != StatusSala.ATIVA) {
            throw new IllegalStateException("Sala indisponível");
        }
    }

}
