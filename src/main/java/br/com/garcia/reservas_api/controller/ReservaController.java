package br.com.garcia.reservas_api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.garcia.reservas_api.dto.ReservaRequestDTO;
import br.com.garcia.reservas_api.dto.ReservaResponseDTO;
import br.com.garcia.reservas_api.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(summary = "Criar Reserva", description = "Endpoint para criar uma nova reserva.")
    @PostMapping("/criar")
    public ResponseEntity<ReservaResponseDTO> criarReserva(@Valid @RequestBody ReservaRequestDTO reservaRequestDTO, UriComponentsBuilder uriBuilder) {
        ReservaResponseDTO reservaResponse = reservaService.criarReserva(reservaRequestDTO);
        // Cria a URI do recurso recém-criado.
        // Exemplo: se o id for 10, a URI gerada será "/reservas/10".
        URI uri = uriBuilder.path("/reservas/{id}").buildAndExpand(reservaResponse.id()).toUri(); 
        return ResponseEntity.created(uri).body(reservaResponse);
    }

    @Operation(summary = "Listar Reservas", description = "Endpoint para listar todas as reservas.")
    @GetMapping()
    public ResponseEntity<List<ReservaResponseDTO>> listarReservas() {
        List<ReservaResponseDTO> reservasList = reservaService.listarReservas();
        return ResponseEntity.ok().body(reservasList);
    }

}