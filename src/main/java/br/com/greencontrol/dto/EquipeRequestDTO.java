package br.com.greencontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipeRequestDTO(
        @NotBlank(message = "O nome da equipe é obrigatório.")
        @Size(max = 100, message = "O nome não pode exceder 100 caracteres.")
        @br.com.greencontrol.validation.OracleText(max = 100)
        String nome,

        @NotBlank(message = "A especialidade da equipe é obrigatória.")
        @Size(max = 100, message = "A especialidade não pode exceder 100 caracteres.")
        @br.com.greencontrol.validation.OracleText(max = 100)
        String especialidade
) {}
