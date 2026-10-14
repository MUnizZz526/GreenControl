package br.com.greencontrol.dto;

import br.com.greencontrol.model.TrechoRodovia;

public record TrechoResponseDTO(
        Long id,
        Double quilometroInicial,
        Double quilometroFinal,
        Double nivelVegetacao,
        String tipoClima,
        String nivelPrioridade,
        Long idEquipe,
        String nomeEquipe
) {
    public static TrechoResponseDTO fromEntity(TrechoRodovia trecho) {
        if (trecho == null) return null;
        Long equipeId = trecho.getEquipe() != null ? trecho.getEquipe().getId() : null;
        String equipeNome = trecho.getEquipe() != null ? trecho.getEquipe().getNome() : null;

        return new TrechoResponseDTO(
                trecho.getId(),
                trecho.getQuilometroInicial(),
                trecho.getQuilometroFinal(),
                trecho.getNivelVegetacao(),
                trecho.getTipoClima(),
                trecho.obterNivelPrioridade(),
                equipeId,
                equipeNome
        );
    }
}
