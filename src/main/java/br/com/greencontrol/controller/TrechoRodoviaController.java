package br.com.greencontrol.controller;

import br.com.greencontrol.dto.TrechoRequestDTO;
import br.com.greencontrol.dto.TrechoResponseDTO;
import br.com.greencontrol.service.TrechoRodoviaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/trechos")
public class TrechoRodoviaController {

    private final TrechoRodoviaService trechoService;

    public TrechoRodoviaController(TrechoRodoviaService trechoService) {
        this.trechoService = trechoService;
    }

    @GetMapping
    public ResponseEntity<List<TrechoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(trechoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrechoResponseDTO> buscarPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(trechoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<TrechoResponseDTO> criar(@RequestBody @Valid TrechoRequestDTO dto,
                                                  UriComponentsBuilder uriBuilder) {
        TrechoResponseDTO criado = trechoService.criar(dto);
        URI uri = uriBuilder.path("/api/trechos/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrechoResponseDTO> atualizar(@PathVariable @Positive Long id,
                                                      @RequestBody @Valid TrechoRequestDTO dto) {
        return ResponseEntity.ok(trechoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable @Positive Long id) {
        trechoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/simular-crescimento")
    public ResponseEntity<TrechoResponseDTO> simularCrescimento(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(trechoService.simularCrescimento(id));
    }

    @GetMapping("/clima")
    public ResponseEntity<List<TrechoResponseDTO>> buscarPorClima(@RequestParam String tipo) {
        return ResponseEntity.ok(trechoService.buscarPorClima(tipo));
    }

    @GetMapping("/vegetacao-minima")
    public ResponseEntity<List<TrechoResponseDTO>> buscarPorVegetacaoMinima(@RequestParam @PositiveOrZero Double minimo) {
        return ResponseEntity.ok(trechoService.buscarPorNivelMinimo(minimo));
    }
}
