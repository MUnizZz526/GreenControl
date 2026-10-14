package br.com.greencontrol.controller;

import br.com.greencontrol.dto.IntervencaoRequestDTO;
import br.com.greencontrol.dto.IntervencaoResponseDTO;
import br.com.greencontrol.service.IntervencaoOperacionalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/intervencoes")
public class IntervencaoOperacionalController {

    private final IntervencaoOperacionalService intervencaoService;

    public IntervencaoOperacionalController(IntervencaoOperacionalService intervencaoService) {
        this.intervencaoService = intervencaoService;
    }

    @GetMapping
    public ResponseEntity<List<IntervencaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(intervencaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntervencaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(intervencaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<IntervencaoResponseDTO> criar(@RequestBody @Valid IntervencaoRequestDTO dto,
                                                        UriComponentsBuilder uriBuilder) {
        IntervencaoResponseDTO criada = intervencaoService.criar(dto);
        URI uri = uriBuilder.path("/api/intervencoes/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntervencaoResponseDTO> atualizar(@PathVariable Long id,
                                                            @RequestBody @Valid IntervencaoRequestDTO dto) {
        return ResponseEntity.ok(intervencaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        intervencaoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<IntervencaoResponseDTO>> buscarPorTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(intervencaoService.buscarPorTipo(tipo));
    }

    @GetMapping("/trecho/{trechoId}")
    public ResponseEntity<List<IntervencaoResponseDTO>> buscarPorTrecho(@PathVariable Long trechoId) {
        return ResponseEntity.ok(intervencaoService.buscarPorTrecho(trechoId));
    }
}
