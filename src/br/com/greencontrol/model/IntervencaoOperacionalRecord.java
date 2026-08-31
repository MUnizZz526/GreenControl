package br.com.greencontrol.model;

import java.sql.Date;

public record IntervencaoOperacionalRecord(
        Long id,
        String tipoIntervencao,
        Date dataIntervencao,
        Long idTrecho
) {}