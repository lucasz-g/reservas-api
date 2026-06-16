package br.com.garcia.reservas_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SalaRequestDTO(
    @NotBlank(message = "O nome da sala é obrigatório")
    String nome, 
    @NotNull(message = "A capacidade da sala é obrigatória")
    @Min(value = 1, message = "A capacidade da sala deve ser pelo menos 1")
    Integer capacidade, 
    @NotBlank(message = "A localização da sala é obrigatória")
    @Size(max = 100, message = "A localização deve ter no máximo 100 caracteres")
    String localizacao) {
}
