package br.com.garcia.reservas_api.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.garcia.reservas_api.entity.Reserva;
import br.com.garcia.reservas_api.entity.StatusReserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsBySala_IdAndDataReservaAndStatusAndHoraInicioLessThanAndHoraFimGreaterThan(
            Long salaId,
            LocalDate dataReserva,
            StatusReserva status,
            LocalTime horaFim,
            LocalTime horaInicio);
}
