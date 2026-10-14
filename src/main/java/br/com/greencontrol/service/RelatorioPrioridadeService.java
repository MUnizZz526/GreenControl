package br.com.greencontrol.service;

import br.com.greencontrol.dto.RelatorioResponseDTO;
import br.com.greencontrol.model.RelatorioPrioridade;
import br.com.greencontrol.model.TrechoRodovia;
import br.com.greencontrol.repository.RelatorioPrioridadeRepository;
import br.com.greencontrol.repository.TrechoRodoviaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RelatorioPrioridadeService {

    private final RelatorioPrioridadeRepository relatorioRepository;
    private final TrechoRodoviaRepository trechoRepository;

    public RelatorioPrioridadeService(RelatorioPrioridadeRepository relatorioRepository,
                                      TrechoRodoviaRepository trechoRepository) {
        this.relatorioRepository = relatorioRepository;
        this.trechoRepository = trechoRepository;
    }

    /**
     * Motor de Prioridade (Sprint 2/3 transformado em Serviço Spring):
     * Classifica todos os trechos atuais do banco de acordo com a altura da vegetação
     * e grava um novo snapshot consolidado no histórico do Oracle.
     */
    @Transactional
    public RelatorioResponseDTO gerarRelatorio() {
        List<TrechoRodovia> trechos = trechoRepository.findAll();

        int qtUrgente = 0;
        int qtCritico = 0;
        int qtAtencao = 0;
        int qtNormal = 0;

        for (TrechoRodovia t : trechos) {
            double veg = t.getNivelVegetacao() != null ? t.getNivelVegetacao() : 0.0;
            if (veg > 60.0) {
                qtUrgente++;
            } else if (veg > 30.0) {
                qtCritico++;
            } else if (veg >= 15.0) {
                qtAtencao++;
            } else {
                qtNormal++;
            }
        }

        String resumo = "Análise de prioridade realizada para " + trechos.size() + " trecho(s) monitorado(s).";

        RelatorioPrioridade relatorio = new RelatorioPrioridade(
                qtUrgente,
                qtCritico,
                qtAtencao,
                qtNormal,
                resumo
        );

        RelatorioPrioridade salvo = relatorioRepository.save(relatorio);
        return RelatorioResponseDTO.fromEntity(salvo);
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> listarHistorico() {
        return relatorioRepository.findAllByOrderByDataGeracaoDesc()
                .stream()
                .map(RelatorioResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponseDTO> consultarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioRepository.findByDataGeracaoBetween(inicio, fim)
                .stream()
                .map(RelatorioResponseDTO::fromEntity)
                .toList();
    }
}
