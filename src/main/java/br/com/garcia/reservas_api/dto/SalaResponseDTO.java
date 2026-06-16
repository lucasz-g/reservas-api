package br.com.garcia.reservas_api.dto;

import br.com.garcia.reservas_api.entity.StatusSala;

public record SalaResponseDTO(
        Long id,
        String nome,
        Integer capacidade,
        String localizacao, 
        StatusSala status) {
}
