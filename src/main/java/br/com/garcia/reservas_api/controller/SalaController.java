package br.com.garcia.reservas_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.garcia.reservas_api.dto.SalaRequestDTO;
import br.com.garcia.reservas_api.dto.SalaResponseDTO;
import br.com.garcia.reservas_api.service.SalaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @Operation(summary = "Listar Salas", description = "Endpoint para listar todas as salas disponíveis.")
    @GetMapping
    public ResponseEntity<List<SalaResponseDTO>> listarSalas() {
        return ResponseEntity.status(200).body(salaService.listarSalas());
    }

    @GetMapping(params = "id")
    @Operation(summary = "Buscar Sala por ID", description = "Endpoint para buscar uma sala específica pelo seu ID.")
    public ResponseEntity<SalaResponseDTO> buscarSalaPorId(@RequestParam Long id) {
        return ResponseEntity.ok(salaService.buscarSalaPorId(id)); // Substituir null pela sala encontrada
    }
    

    @Operation(summary = "Criar Sala", description = "Endpoint para criar uma nova sala.")
    @PostMapping("/criar")
    public ResponseEntity<SalaResponseDTO> criarSala(@Valid @RequestBody SalaRequestDTO salaRequestDTO) {
        SalaResponseDTO criada = salaService.criarSala(salaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

}
