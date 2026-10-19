package br.com.greencontrol.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static ConexaoBD instancia;
    private Connection conexao;

    private final String URL = System.getenv("DB_URL");


    private final String USER = System.getenv("DB_USER"); // Coloque seu RM (exemplo: "RM123456")
    private final String PASS = System.getenv("DB_PASSWORD");

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
            System.err.println("Driver JDBC não encontrado. Verifique se o ojdbc17.jar está no classpath: ");
        } catch (SQLException e) {
            System.err.println("Erro de conexão no Oracle FIAP: ");
        }
        return conexao;
    }

    public void desconectar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: ");
        }
    }
}