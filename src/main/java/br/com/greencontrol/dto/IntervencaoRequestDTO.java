package br.com.greencontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record IntervencaoRequestDTO(
        @NotBlank(message = "O tipo de intervenção é obrigatório.")
        String tipoIntervencao,

        LocalDate dataIntervencao,

        @NotNull(message = "O ID do trecho é obrigatório.")
        Long idTrecho
) {}
