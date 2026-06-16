package br.com.garcia.reservas_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.garcia.reservas_api.entity.Sala;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    
}
