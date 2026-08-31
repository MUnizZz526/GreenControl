package br.com.greencontrol.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static ConexaoBD instancia;
    private Connection conexao;

    // Configuração com o banco Oracle da FIAP fornecido pelo professor
    private final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";

    // TODO: Substitua pelos seus dados reais da FIAP
    private final String USER = "RM566527"; // Coloque seu RM (exemplo: "RM123456")
    private final String PASS = "270806";   // Coloque sua data de nascimento (exemplo: "150804")

    private ConexaoBD() {}

    public static synchronized ConexaoBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBD();
        }
        return instancia;
    }

    public Connection conectar() {
        try {
            if (conexao == null || conexao.isClosed()) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conexao = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado. Verifique se o ojdbc17.jar está no classpath: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro de conexão no Oracle FIAP: " + e.getMessage());
        }
        return conexao;
    }

    public void desconectar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}