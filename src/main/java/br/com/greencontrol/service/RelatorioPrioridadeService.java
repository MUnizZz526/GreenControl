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
            switch (t.obterNivelPrioridade()) {
                case "URGENTE" -> qtUrgente++;
                case "CRITICO" -> qtCritico++;
                case "ATENCAO" -> qtAtencao++;
                default -> qtNormal++;
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
    public List<RelatorioResponseDTO> consultarPorPeriodo(String inicio, String fim) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime de = parse(inicio, agora.minusMonths(1), false);
        LocalDateTime ate = parse(fim, agora, true);
        if (de.isAfter(ate) || (fim != null && !fim.contains("T") && !de.isBefore(ate))) throw new br.com.greencontrol.exception.RegraDeNegocioException("Início deve ser anterior ou igual ao fim.");
        var resultados = fim != null && !fim.contains("T")
                ? relatorioRepository.findByDataGeracaoGreaterThanEqualAndDataGeracaoLessThan(de, ate)
                : relatorioRepository.findByDataGeracaoBetween(de, ate);
        return resultados.stream().map(RelatorioResponseDTO::fromEntity).toList();
    }

    private LocalDateTime parse(String valor, LocalDateTime padrao, boolean finalPeriodo) {
        if (valor == null) return padrao;
        try {
            if (valor.contains("T")) return LocalDateTime.parse(valor);
            var dia = java.time.LocalDate.parse(valor);
            return (finalPeriodo ? dia.plusDays(1) : dia).atStartOfDay();
        } catch (java.time.DateTimeException ex) {
            throw new br.com.greencontrol.exception.RegraDeNegocioException("Data inválida. Use yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ss.");
        }
    }
}
