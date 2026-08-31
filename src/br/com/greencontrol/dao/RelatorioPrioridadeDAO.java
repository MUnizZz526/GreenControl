package br.com.greencontrol.dao;

import br.com.greencontrol.db.ConexaoBD;
import br.com.greencontrol.model.RelatorioPrioridadeRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioPrioridadeDAO {
    private static final String INSERT_SQL = "INSERT INTO T_RELATORIO_PRIORIDADE (QT_URGENTE, QT_CRITICO, QT_ATENCAO, QT_NORMAL, RESUMO) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM T_RELATORIO_PRIORIDADE ORDER BY DATA_GERACAO DESC";

    public void salvarRelatorio(int qtUrgente, int qtCritico, int qtAtencao, int qtNormal, String resumo) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, qtUrgente);
            stmt.setInt(2, qtCritico);
            stmt.setInt(3, qtAtencao);
            stmt.setInt(4, qtNormal);
            stmt.setString(5, resumo);
            stmt.executeUpdate();
            System.out.println("[BD] Relatório de prioridade salvo com sucesso no Oracle!");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar relatório no BD: " + e.getMessage());
        }
    }

    public List<RelatorioPrioridadeRecord> listarTodas() {
        List<RelatorioPrioridadeRecord> lista = new ArrayList<>();
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new RelatorioPrioridadeRecord(
                        rs.getLong("ID_RELATORIO"),
                        rs.getInt("QT_URGENTE"),
                        rs.getInt("QT_CRITICO"),
                        rs.getInt("QT_ATENCAO"),
                        rs.getInt("QT_NORMAL"),
                        rs.getString("RESUMO"),
                        rs.getTimestamp("DATA_GERACAO")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar relatórios: " + e.getMessage());
        }
        return lista;
    }
}