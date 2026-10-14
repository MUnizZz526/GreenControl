package br.com.greencontrol.dao;

import br.com.greencontrol.db.ConexaoBD;
import br.com.greencontrol.model.TrechoRodoviaRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrechoRodoviaDAO {

    private static final String SQL_INSERT =
            "INSERT INTO T_TRECHO_RODOVIA (QUILOMETRO_INICIAL, QUILOMETRO_FINAL, NIVEL_VEGETACAO, TIPO_CLIMA, ID_EQUIPE) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL =
            "SELECT ID_TRECHO, QUILOMETRO_INICIAL, QUILOMETRO_FINAL, NIVEL_VEGETACAO, TIPO_CLIMA, ID_EQUIPE FROM T_TRECHO_RODOVIA";
    private static final String SQL_SELECT_BY_ID =
            "SELECT ID_TRECHO, QUILOMETRO_INICIAL, QUILOMETRO_FINAL, NIVEL_VEGETACAO, TIPO_CLIMA, ID_EQUIPE FROM T_TRECHO_RODOVIA WHERE ID_TRECHO = ?";
    private static final String SQL_UPDATE =
            "UPDATE T_TRECHO_RODOVIA SET QUILOMETRO_INICIAL = ?, QUILOMETRO_FINAL = ?, NIVEL_VEGETACAO = ?, TIPO_CLIMA = ?, ID_EQUIPE = ? WHERE ID_TRECHO = ?";
    private static final String SQL_DELETE =
            "DELETE FROM T_TRECHO_RODOVIA WHERE ID_TRECHO = ?";

    public void inserir(TrechoRodoviaRecord trecho) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setDouble(1, trecho.kmInicial());
            stmt.setDouble(2, trecho.kmFinal());
            stmt.setDouble(3, trecho.nivelVegetacao());
            stmt.setString(4, trecho.tipoClima());
            if (trecho.idEquipe() != null) {
                stmt.setLong(5, trecho.idEquipe());
            } else {
                stmt.setNull(5, Types.NUMERIC);
            }
            stmt.executeUpdate();
            System.out.println("Trecho inserido com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao inserir trecho: " + e.getMessage());
        } finally {
            fecharRecursos(stmt, null);
        }
    }

    public List<TrechoRodoviaRecord> listarTodas() {
        List<TrechoRodoviaRecord> lista = new ArrayList<>();
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_SELECT_ALL);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("ID_TRECHO");
                double kmInicial = rs.getDouble("QUILOMETRO_INICIAL");
                double kmFinal = rs.getDouble("QUILOMETRO_FINAL");
                double nivelVegetacao = rs.getDouble("NIVEL_VEGETACAO");
                String tipoClima = rs.getString("TIPO_CLIMA");
                Long idEquipe = rs.getLong("ID_EQUIPE");
                if (rs.wasNull()) idEquipe = null;

                lista.add(new TrechoRodoviaRecord(id, kmInicial, kmFinal, nivelVegetacao, tipoClima, idEquipe));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar trechos: " + e.getMessage());
        } finally {
            fecharRecursos(stmt, rs);
        }
        return lista;
    }

    public TrechoRodoviaRecord buscarPorId(Long idBusca) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SQL_SELECT_BY_ID);
            stmt.setLong(1, idBusca);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("ID_TRECHO");
                double kmInicial = rs.getDouble("QUILOMETRO_INICIAL");
                double kmFinal = rs.getDouble("QUILOMETRO_FINAL");
                double nivelVegetacao = rs.getDouble("NIVEL_VEGETACAO");
                String tipoClima = rs.getString("TIPO_CLIMA");
                Long idEquipe = rs.getLong("ID_EQUIPE");
                if (rs.wasNull()) idEquipe = null;

                return new TrechoRodoviaRecord(id, kmInicial, kmFinal, nivelVegetacao, tipoClima, idEquipe);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar trecho: " + e.getMessage());
        } finally {
            fecharRecursos(stmt, rs);
        }
        return null;
    }

    public void atualizar(TrechoRodoviaRecord trecho) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setDouble(1, trecho.kmInicial());
            stmt.setDouble(2, trecho.kmFinal());
            stmt.setDouble(3, trecho.nivelVegetacao());
            stmt.setString(4, trecho.tipoClima());
            if (trecho.idEquipe() != null) {
                stmt.setLong(5, trecho.idEquipe());
            } else {
                stmt.setNull(5, Types.NUMERIC);
            }
            stmt.setLong(6, trecho.id());
            stmt.executeUpdate();
            System.out.println("Trecho atualizado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar trecho: " + e.getMessage());
        } finally {
            fecharRecursos(stmt, null);
        }
    }

    public void deletar(Long id) {
        Connection conn = ConexaoBD.getInstancia().conectar();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(SQL_DELETE);
            stmt.setLong(1, id);
            stmt.executeUpdate();
            System.out.println("Trecho deletado com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao deletar trecho: " + e.getMessage());
        } finally {
            fecharRecursos(stmt, null);
        }
    }

    private void fecharRecursos(PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar recursos: " + e.getMessage());
        }
    }
}