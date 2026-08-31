package br.com.greencontrol.main;

import br.com.greencontrol.TrechoRodovia;
import br.com.greencontrol.dao.*;
import br.com.greencontrol.db.ConexaoBD;
import br.com.greencontrol.model.*;
import br.com.greencontrol.service.GeradorRelatorio;

import java.sql.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SPRINT 3 - MOTIVA (PERSISTÊNCIA JDBC) ===\n");

        ConexaoBD conexao = ConexaoBD.getInstancia();
        conexao.conectar();

        System.out.println("--- [1/4] Testando EquipeManutencaoDAO ---");
        EquipeManutencaoDAO daoEquipe = new EquipeManutencaoDAO();
        daoEquipe.inserir(new EquipeManutencaoRecord(null, "Equipe Alfa", "Roçada Mecanizada"));

        List<EquipeManutencaoRecord> equipes = daoEquipe.listarTodas();
        equipes.forEach(e -> System.out.println("Equipe encontrada: " + e));

        System.out.println("\n--- [2/4] Testando TrechoRodoviaDAO ---");
        TrechoRodoviaDAO daoTrecho = new TrechoRodoviaDAO();
        Long idEquipe = equipes.isEmpty() ? null : equipes.get(0).id();
        daoTrecho.inserir(new TrechoRodoviaRecord(null, 10.0, 25.0, 35.0, "Úmido", idEquipe));

        List<TrechoRodoviaRecord> trechosBD = daoTrecho.listarTodas();
        trechosBD.forEach(t -> System.out.println("Trecho no BD: " + t));

        System.out.println("\n--- [3/4] Testando IntervencaoOperacionalDAO ---");
        IntervencaoOperacionalDAO daoIntervencao = new IntervencaoOperacionalDAO();
        if (!trechosBD.isEmpty()) {
            Long idTrecho = trechosBD.get(0).id();
            daoIntervencao.inserir(new IntervencaoOperacionalRecord(null, "Roçada Mecanizada", new Date(System.currentTimeMillis()), idTrecho));
        }
        daoIntervencao.listarTodas().forEach(i -> System.out.println("Intervenção no BD: " + i));

        System.out.println("\n--- [4/4] Executando GeradorRelatorio com Persistência ---");
        TrechoRodovia[] trechosArray = new TrechoRodovia[trechosBD.size()];
        for (int i = 0; i < trechosBD.size(); i++) {
            TrechoRodoviaRecord rec = trechosBD.get(i);
            trechosArray[i] = new TrechoRodovia(rec.kmInicial(), rec.kmFinal(), rec.nivelVegetacao(), rec.tipoClima());
        }

        GeradorRelatorio gerador = new GeradorRelatorio();
        gerador.gerarRelatorio(trechosArray);

        System.out.println("\n--- Histórico de Relatórios Salvos no Oracle ---");
        RelatorioPrioridadeDAO daoRelatorio = new RelatorioPrioridadeDAO();
        daoRelatorio.listarTodas().forEach(r -> System.out.println(r));

        conexao.desconectar();
        System.out.println("\n=== SPRINT 3 FINALIZADA COM SUCESSO ===");
    }
}