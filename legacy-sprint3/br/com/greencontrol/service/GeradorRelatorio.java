package br.com.greencontrol.service;

import br.com.greencontrol.TrechoRodovia;
import br.com.greencontrol.dao.RelatorioPrioridadeDAO;

public class GeradorRelatorio {

    public void gerarRelatorio(TrechoRodovia[] trechos) {
        int qtUrgente = 0;
        int qtCritico = 0;
        int qtAtencao = 0;
        int qtNormal = 0;

        for (TrechoRodovia t : trechos) {
            if (t == null) continue;
            double veg = t.getNivelVegetacao();

            if (veg > 60.0) {
                qtUrgente++;
            } else if (veg > 30.0) {
                qtCritico++;
            } else if (veg >= 15.0) {
                qtAtencao++;
            } else {
                qtNormal++;
            }
        }

        String resumo = "Análise realizada para " + trechos.length + " trecho(s).";

        // Exibe no console
        System.out.println("\n===== RELATÓRIO DE PRIORIDADE =====");
        System.out.println("Urgente: " + qtUrgente + " | Crítico: " + qtCritico + " | Atenção: " + qtAtencao + " | Normal: " + qtNormal);
        System.out.println("Resumo:  " + resumo);

        // Salva no banco de dados Oracle
        RelatorioPrioridadeDAO dao = new RelatorioPrioridadeDAO();
        dao.salvarRelatorio(qtUrgente, qtCritico, qtAtencao, qtNormal, resumo);
    }
}