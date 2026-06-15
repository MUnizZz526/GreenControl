package br.com.greencontrol;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("===  INICIANDO SIMULAÇÃO DA SPRINT 2 ===\n");

        TrechoRodovia t1 = new TrechoRodovia(0, 10, 12.0, "Seco");
        TrechoRodovia t2 = new TrechoRodovia(10, 20, 25.0, "Úmido"); // Vai crescer mais rápido!
        TrechoRodovia t3 = new TrechoRodovia(20, 30, 5.0, "Seco");

        List<TrechoRodovia> rodovia = new ArrayList<>();
        rodovia.add(t1);
        rodovia.add(t2);
        rodovia.add(t3);

        System.out.println("--- LENDO SENSORES VIA IOT ---");
        for (TrechoRodovia trecho : rodovia) {
            trecho.transmitirDadosSensor();
        }


        System.out.println("\n--- RELATÓRIO DE PRIORIDADE AUTOMÁTICO ---");
        for (TrechoRodovia trecho : rodovia) {
            System.out.println("\nTrecho: KM " + trecho.getQuilometroInicial() + " ao " + trecho.getQuilometroFinal() + " (" + trecho.getTipoClima() + ")");
            System.out.println("Altura da Vegetação: " + trecho.getNivelVegetacao() + "cm");

            IntervencaoOperacional intervencao = null;

            if (trecho.getNivelVegetacao() > 30.0) {
                System.out.println("STATUS : PRIORIDADE ALTA");
                intervencao = new RocadaMecanizada(); // Polimorfismo
            } else if (trecho.getNivelVegetacao() >= 15.0) {
                System.out.println("STATUS : PRIORIDADE MÉDIA");
                intervencao = new Pulverizacao();     // Polimorfismo
            } else {
                System.out.println("STATUS : SITUAÇÃO CONTROLADA");
            }

            if (intervencao != null) {
                intervencao.executarServico();
            }
        }
    }
}