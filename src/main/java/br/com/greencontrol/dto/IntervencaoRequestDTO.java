package br.com.greencontrol.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record IntervencaoRequestDTO(
        @NotBlank(message = "O tipo de intervenção é obrigatório.")
        @Size(max = 100)
        @br.com.greencontrol.validation.OracleText(max = 100)
        String tipoIntervencao,

        LocalDate dataIntervencao,

        @NotNull(message = "O ID do trecho é obrigatório.")
        @Positive
        Long idTrecho
) {}
