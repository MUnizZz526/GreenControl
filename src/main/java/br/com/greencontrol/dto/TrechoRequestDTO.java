package br.com.greencontrol.dto;

import jakarta.validation.constraints.*;

public record TrechoRequestDTO(
        @NotNull(message = "Quilômetro inicial é obrigatório.")
        @PositiveOrZero(message = "Quilômetro inicial não pode ser negativo.")
        @Digits(integer = 6, fraction = 2)
        Double quilometroInicial,

        @NotNull(message = "Quilômetro final é obrigatório.")
        @PositiveOrZero(message = "Quilômetro final não pode ser negativo.")
        @Digits(integer = 6, fraction = 2)
        Double quilometroFinal,

        @NotNull(message = "Nível de vegetação é obrigatório.")
        @PositiveOrZero(message = "Nível de vegetação não pode ser negativo.")
        @Digits(integer = 6, fraction = 2)
        Double nivelVegetacao,

        @NotBlank(message = "Tipo de clima é obrigatório.")
        @Size(max = 50)
        @br.com.greencontrol.validation.OracleText(max = 50)
        String tipoClima,

        @Positive
        Long idEquipe
) {}
