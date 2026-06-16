package br.com.garcia.reservas_api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.garcia.reservas_api.dto.SalaRequestDTO;
import br.com.garcia.reservas_api.dto.SalaResponseDTO;
import br.com.garcia.reservas_api.entity.Sala;
import br.com.garcia.reservas_api.exceptions.RecursoNaoEncontradoException;
import br.com.garcia.reservas_api.repository.SalaRepository;

@Service
public class SalaService {

    private static final Logger logger = LoggerFactory.getLogger(SalaService.class);

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    public List<SalaResponseDTO> listarSalas() {
        // Lógica para listar as salas
        List<Sala> salas = salaRepository.findAll(); // Retorna uma lista de salas;
        // Converter a lista de salas para uma lista de SalaResponseDTO
        return salas.stream()
                .map(this::toResponseDTO) // Converte cada Sala para SalaResponseDTO
                .toList();
    }

    public SalaResponseDTO criarSala(SalaRequestDTO salaRequest) {
        // Transformar o SalaRequestDTO em uma entidade Sala
        Sala novaSala = toEntity(salaRequest);

        // Mapear os campos do SalaRequestDTO para a entidade Sala
        Sala salaSalva = salaRepository.save(novaSala); // Salva a nova sala no banco de dados
        // Transformar a entidade Sala salva em um SalaResponseDTO e retornar
        return toResponseDTO(salaSalva);
    }

    public SalaResponseDTO buscarSalaPorId(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala nao encontrada"));
        // Transformar a entidade Sala encontrada em um SalaResponseDTO e retornar
        return toResponseDTO(sala);
    }

    /** Utils */
    // Método para transformar um SalaRequestDTO em uma entidade Sala
    public Sala toEntity(SalaRequestDTO salaRequest) {
        Sala sala = new Sala();
        sala.setNome(salaRequest.nome());
        sala.setCapacidade(salaRequest.capacidade());
        sala.setLocalizacao(salaRequest.localizacao());
        return sala;
    }

    // Método para converter uma entidade Sala em um SalaResponseDTO
    public SalaResponseDTO toResponseDTO(Sala sala) {
        return new SalaResponseDTO(
                sala.getId(),
                sala.getNome(),
                sala.getCapacidade(),
                sala.getLocalizacao(),
                sala.getStatus());
    }

}
