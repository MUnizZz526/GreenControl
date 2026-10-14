package br.com.greencontrol.dao;

import br.com.greencontrol.db.ConexaoBD;
import br.com.greencontrol.model.IntervencaoOperacionalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IntervencaoOperacionalDAO {
    private static final String INSERT_SQL = "INSERT INTO T_INTERVENCAO_OPERACIONAL (TIPO_INTERVENCAO, DATA_INTERVENCAO, ID_TRECHO) VALUES (?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM T_INTERVENCAO_OPERACIONAL WHERE ID_INTERVENCAO = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM T_INTERVENCAO_OPERACIONAL";
    private static final String UPDATE_SQL = "UPDATE T_INTERVENCAO_OPERACIONAL SET TIPO_INTERVENCAO = ?, DATA_INTERVENCAO = ?, ID_TRECHO = ? WHERE ID_INTERVENCAO = ?";
    private static final String DELETE_SQL = "DELETE FROM T_INTERVENCAO_OPERACIONAL WHERE ID_INTERVENCAO = ?";

    public void inserir(IntervencaoOperacionalRecord intervencao) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, intervencao.tipoIntervencao());
            stmt.setDate(2, intervencao.dataIntervencao());
            stmt.setLong(3, intervencao.idTrecho());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir intervenção: " + e.getMessage());
        }
    }

    public IntervencaoOperacionalRecord buscarPorId(Long id) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new IntervencaoOperacionalRecord(
                            rs.getLong("ID_INTERVENCAO"),
                            rs.getString("TIPO_INTERVENCAO"),
                            rs.getDate("DATA_INTERVENCAO"),
                            rs.getLong("ID_TRECHO")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar intervenção por ID: " + e.getMessage());
        }
        return null;
    }

    public List<IntervencaoOperacionalRecord> listarTodas() {
        List<IntervencaoOperacionalRecord> lista = new ArrayList<>();
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new IntervencaoOperacionalRecord(
                        rs.getLong("ID_INTERVENCAO"),
                        rs.getString("TIPO_INTERVENCAO"),
                        rs.getDate("DATA_INTERVENCAO"),
                        rs.getLong("ID_TRECHO")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar intervenções: " + e.getMessage());
        }
        return lista;
    }

    public void atualizar(IntervencaoOperacionalRecord intervencao) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, intervencao.tipoIntervencao());
            stmt.setDate(2, intervencao.dataIntervencao());
            stmt.setLong(3, intervencao.idTrecho());
            stmt.setLong(4, intervencao.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar intervenção: " + e.getMessage());
        }
    }

    public void deletar(Long id) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar intervenção: " + e.getMessage());
        }
    }
}