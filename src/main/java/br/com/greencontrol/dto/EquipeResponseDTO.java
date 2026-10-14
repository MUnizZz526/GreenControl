package br.com.greencontrol.dto;

import br.com.greencontrol.model.EquipeManutencao;

public record EquipeResponseDTO(
        Long id,
        String nome,
        String especialidade
) {
    public static EquipeResponseDTO fromEntity(EquipeManutencao equipe) {
        if (equipe == null) return null;
        return new EquipeResponseDTO(
                equipe.getId(),
                equipe.getNome(),
                equipe.getEspecialidade()
        );
    }
}
