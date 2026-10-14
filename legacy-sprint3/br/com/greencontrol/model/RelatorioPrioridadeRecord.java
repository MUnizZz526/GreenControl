package br.com.greencontrol.model;

import java.sql.Timestamp;

public record RelatorioPrioridadeRecord(
        Long id,
        int qtUrgente,
        int qtCritico,
        int qtAtencao,
        int qtNormal,
        String resumo,
        Timestamp dataGeracao
) {}