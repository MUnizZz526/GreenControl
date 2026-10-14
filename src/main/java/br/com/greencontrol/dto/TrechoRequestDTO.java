package br.com.greencontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record TrechoRequestDTO(
        @NotNull(message = "Quilômetro inicial é obrigatório.")
        @PositiveOrZero(message = "Quilômetro inicial não pode ser negativo.")
        Double quilometroInicial,

        @NotNull(message = "Quilômetro final é obrigatório.")
        @PositiveOrZero(message = "Quilômetro final não pode ser negativo.")
        Double quilometroFinal,

        @NotNull(message = "Nível de vegetação é obrigatório.")
        @PositiveOrZero(message = "Nível de vegetação não pode ser negativo.")
        Double nivelVegetacao,

        @NotBlank(message = "Tipo de clima é obrigatório.")
        String tipoClima,

        Long idEquipe
) {}
