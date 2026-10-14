package br.com.greencontrol.dto;

import br.com.greencontrol.model.IntervencaoOperacional;
import java.time.LocalDate;

public record IntervencaoResponseDTO(
        Long id,
        String tipoIntervencao,
        LocalDate dataIntervencao,
        Long idTrecho,
        Double trechoKmInicial,
        Double trechoKmFinal
) {
    public static IntervencaoResponseDTO fromEntity(IntervencaoOperacional intervencao) {
        if (intervencao == null) return null;
        Long idTrecho = intervencao.getTrecho() != null ? intervencao.getTrecho().getId() : null;
        Double kmInicial = intervencao.getTrecho() != null ? intervencao.getTrecho().getQuilometroInicial() : null;
        Double kmFinal = intervencao.getTrecho() != null ? intervencao.getTrecho().getQuilometroFinal() : null;

        return new IntervencaoResponseDTO(
                intervencao.getId(),
                intervencao.getTipoIntervencao(),
                intervencao.getDataIntervencao(),
                idTrecho,
                kmInicial,
                kmFinal
        );
    }
}
