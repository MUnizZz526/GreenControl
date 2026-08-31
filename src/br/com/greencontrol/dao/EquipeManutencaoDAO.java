package br.com.greencontrol.dao;

import br.com.greencontrol.db.ConexaoBD;
import br.com.greencontrol.model.EquipeManutencaoRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeManutencaoDAO {
    private static final String INSERT_SQL = "INSERT INTO T_EQUIPE_MANUTENCAO (NOME, ESPECIALIDADE) VALUES (?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM T_EQUIPE_MANUTENCAO WHERE ID_EQUIPE = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM T_EQUIPE_MANUTENCAO";
    private static final String UPDATE_SQL = "UPDATE T_EQUIPE_MANUTENCAO SET NOME = ?, ESPECIALIDADE = ? WHERE ID_EQUIPE = ?";
    private static final String DELETE_SQL = "DELETE FROM T_EQUIPE_MANUTENCAO WHERE ID_EQUIPE = ?";

    public void inserir(EquipeManutencaoRecord equipe) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, equipe.nome());
            stmt.setString(2, equipe.especialidade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir equipe: " + e.getMessage());
        }
    }

    public EquipeManutencaoRecord buscarPorId(Long id) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EquipeManutencaoRecord(
                            rs.getLong("ID_EQUIPE"),
                            rs.getString("NOME"),
                            rs.getString("ESPECIALIDADE")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar equipe por ID: " + e.getMessage());
        }
        return null;
    }

    public List<EquipeManutencaoRecord> listarTodas() {
        List<EquipeManutencaoRecord> lista = new ArrayList<>();
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new EquipeManutencaoRecord(
                        rs.getLong("ID_EQUIPE"),
                        rs.getString("NOME"),
                        rs.getString("ESPECIALIDADE")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar equipes: " + e.getMessage());
        }
        return lista;
    }

    public void atualizar(EquipeManutencaoRecord equipe) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, equipe.nome());
            stmt.setString(2, equipe.especialidade());
            stmt.setLong(3, equipe.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar equipe: " + e.getMessage());
        }
    }

    public void deletar(Long id) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar equipe: " + e.getMessage());
        }
    }
}