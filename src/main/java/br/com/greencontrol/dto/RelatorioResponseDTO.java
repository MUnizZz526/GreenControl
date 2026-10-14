package br.com.greencontrol.dto;

import br.com.greencontrol.model.RelatorioPrioridade;
import java.time.LocalDateTime;

public record RelatorioResponseDTO(
        Long id,
        Integer qtUrgente,
        Integer qtCritico,
        Integer qtAtencao,
        Integer qtNormal,
        String resumo,
        LocalDateTime dataGeracao
) {
    public static RelatorioResponseDTO fromEntity(RelatorioPrioridade relatorio) {
        if (relatorio == null) return null;
        return new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getQtUrgente(),
                relatorio.getQtCritico(),
                relatorio.getQtAtencao(),
                relatorio.getQtNormal(),
                relatorio.getResumo(),
                relatorio.getDataGeracao()
        );
    }
}
