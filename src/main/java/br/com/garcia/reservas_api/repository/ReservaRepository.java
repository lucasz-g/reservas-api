package br.com.garcia.reservas_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.garcia.reservas_api.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long>{
    
}
