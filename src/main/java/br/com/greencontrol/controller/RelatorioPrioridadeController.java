package br.com.greencontrol.controller;

import br.com.greencontrol.dto.RelatorioResponseDTO;
import br.com.greencontrol.service.RelatorioPrioridadeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioPrioridadeController {

    private final RelatorioPrioridadeService relatorioService;

    public RelatorioPrioridadeController(RelatorioPrioridadeService relatorioService) {
        this.relatorioService = relatorioService;
    }

    /**
     * Gera relatório a partir dos trechos cadastrados no banco,
     * executa as regras do motor de prioridade e persiste no histórico.
     */
    @PostMapping
    public ResponseEntity<RelatorioResponseDTO> gerarRelatorio() {
        RelatorioResponseDTO relatorio = relatorioService.gerarRelatorio();
        return ResponseEntity.status(HttpStatus.CREATED).body(relatorio);
    }

    /**
     * Lista todo o histórico de relatórios gerados (ordem decrescente).
     */
    @GetMapping
    public ResponseEntity<List<RelatorioResponseDTO>> listarHistorico() {
        return ResponseEntity.ok(relatorioService.listarHistorico());
    }

    /**
     * Consulta históricos filtrando por período (data inicial e data final).
     * Suporta tanto formato completo (2026-10-14T00:00:00) quanto data simples (2026-10-14).
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<RelatorioResponseDTO>> consultarPorPeriodo(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim) {

        return ResponseEntity.ok(relatorioService.consultarPorPeriodo(inicio, fim));
    }
}
