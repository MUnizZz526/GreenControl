package br.com.greencontrol.controller;

import br.com.greencontrol.dto.EquipeRequestDTO;
import br.com.greencontrol.dto.EquipeResponseDTO;
import br.com.greencontrol.service.EquipeManutencaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeManutencaoController {

    private final EquipeManutencaoService equipeService;

    public EquipeManutencaoController(EquipeManutencaoService equipeService) {
        this.equipeService = equipeService;
    }

    @GetMapping
    public ResponseEntity<List<EquipeResponseDTO>> listarTodas() {
        return ResponseEntity.ok(equipeService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipeService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EquipeResponseDTO> criar(@RequestBody @Valid EquipeRequestDTO dto,
                                                  UriComponentsBuilder uriBuilder) {
        EquipeResponseDTO criada = equipeService.criar(dto);
        URI uri = uriBuilder.path("/api/equipes/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipeResponseDTO> atualizar(@PathVariable Long id,
                                                      @RequestBody @Valid EquipeRequestDTO dto) {
        return ResponseEntity.ok(equipeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        equipeService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<EquipeResponseDTO>> buscarPorEspecialidade(@RequestParam String especialidade) {
        return ResponseEntity.ok(equipeService.buscarPorEspecialidade(especialidade));
    }
}
