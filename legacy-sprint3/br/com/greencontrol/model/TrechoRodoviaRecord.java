package br.com.greencontrol.model;

public record TrechoRodoviaRecord(
        Long id,
        double kmInicial,
        double kmFinal,
        double nivelVegetacao,
        String tipoClima,
        Long idEquipe
) {}