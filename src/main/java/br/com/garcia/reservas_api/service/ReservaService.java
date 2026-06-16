package br.com.garcia.reservas_api.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.garcia.reservas_api.dto.ReservaRequestDTO;
import br.com.garcia.reservas_api.dto.ReservaResponseDTO;
import br.com.garcia.reservas_api.entity.Reserva;
import br.com.garcia.reservas_api.entity.Sala;
import br.com.garcia.reservas_api.entity.StatusReserva;
import br.com.garcia.reservas_api.exceptions.ConflitoHorarioException;
import br.com.garcia.reservas_api.exceptions.RecursoNaoEncontradoException;
import br.com.garcia.reservas_api.repository.ReservaRepository;
import br.com.garcia.reservas_api.repository.SalaRepository;

@Service
public class ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;

    public ReservaService(ReservaRepository reservaRepository, SalaRepository salaRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
    }

    public ReservaResponseDTO criarReserva(ReservaRequestDTO reservaRequestDTO) {
        logger.info("Criando reserva para sala ID: {}", reservaRequestDTO.salaId());
        Sala sala = buscarSala(reservaRequestDTO.salaId());
        sala.validarReserva();
        validarConflitoHorario(reservaRequestDTO);

        Reserva reservaSalva = reservaRepository.save(toEntity(reservaRequestDTO, sala));
        logger.info("Reserva criada com ID: {}", reservaSalva.getId());
        return toDto(reservaSalva);
    }

    public List<ReservaResponseDTO> listarReservas() {
        logger.info("Listando todas as reservas");
        return reservaRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ReservaResponseDTO buscarReservaPorId(Long id) {
        logger.info("Buscando reserva por ID");
        Reserva reserva = optionalBuscaReserva(id);
        logger.info("Reserva encontrada: ID {}", reserva.getId());
        return toDto(reserva);
    }

    public ReservaResponseDTO cancelarReserva(Long id) {
        // Implementar lógica de autenticação e autorização aqui
        logger.info("Cancelando reserva");
        Reserva reserva = optionalBuscaReserva(id);
        reserva.cancelar();
        Reserva reservaAtualizada = reservaRepository.save(reserva);
        logger.info("Reserva cancelada com ID: {}", reservaAtualizada.getId());
        return toDto(reservaAtualizada);
    }


    // Utilitários
    private Reserva toEntity(ReservaRequestDTO reservaRequestDTO, Sala sala) {
        Reserva reserva = new Reserva();
        reserva.setSala(sala);
        reserva.setNomeSolicitante(reservaRequestDTO.nomeSolicitante());
        reserva.setEmail(reservaRequestDTO.email());
        reserva.setDataReserva(reservaRequestDTO.dataReserva());
        reserva.setHoraInicio(reservaRequestDTO.horaInicio());
        reserva.setHoraFim(reservaRequestDTO.horaFim());
        reserva.setFinalidade(reservaRequestDTO.finalidade());
        return reserva;
    }

    private ReservaResponseDTO toDto(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getNomeSolicitante(),
                reserva.getEmail(),
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                reserva.getFinalidade());
    }

    private Sala buscarSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sala nao encontrada"));
    }

    private void validarConflitoHorario(ReservaRequestDTO reservaRequestDTO) {
        boolean existeConflito = reservaRepository
                .existsBySala_IdAndDataReservaAndStatusAndHoraInicioLessThanAndHoraFimGreaterThan(
                        reservaRequestDTO.salaId(),
                        reservaRequestDTO.dataReserva(),
                        StatusReserva.ATIVA,
                        reservaRequestDTO.horaFim(),
                        reservaRequestDTO.horaInicio());

        if (existeConflito) {
            throw new ConflitoHorarioException("Ja existe reserva para esta sala no horario informado");
        }
    }

    private Reserva optionalBuscaReserva(Long id) {
        Optional<Reserva> reserva = reservaRepository.findById(id);
        return reserva.orElseThrow(() -> new RecursoNaoEncontradoException("Reserva nao encontrada"));
    }
}
