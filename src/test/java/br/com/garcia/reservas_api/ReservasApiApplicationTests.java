package br.com.garcia.reservas_api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.garcia.reservas_api.entity.Reserva;
import br.com.garcia.reservas_api.entity.Sala;
import br.com.garcia.reservas_api.entity.StatusSala;
import br.com.garcia.reservas_api.repository.ReservaRepository;
import br.com.garcia.reservas_api.repository.SalaRepository;

@SpringBootTest
class ReservasApiApplicationTests {

	@Autowired
	private SalaRepository salaRepository;

	@Autowired
	private ReservaRepository reservaRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void flywaySeedCarregaDadosParaOsControllers() {
		assertThat(salaRepository.count()).isEqualTo(3);
		assertThat(reservaRepository.count()).isEqualTo(2);

		Sala sala = salaRepository.findById(1L).orElseThrow();
		assertThat(sala.getNome()).isEqualTo("Sala Reuniao A");

		assertThat(reservaRepository.findAll())
				.extracting(Reserva::getNomeSolicitante)
				.containsExactly("Ana Oliveira", "Carlos Souza");

		Sala novaSala = new Sala();
		novaSala.setNome("Sala Teste");
		novaSala.setCapacidade(4);
		novaSala.setLocalizacao("Bloco Teste");
		novaSala.setStatus(StatusSala.ATIVA);

		Sala salaSalva = salaRepository.save(novaSala);
		assertThat(salaSalva.getId()).isGreaterThan(3L);
	}

}
