package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Fábrica de conexões JDBC com o banco SQLite (arquivo funcionarios.db). */
public class Conexao {

    private static final String URL = "jdbc:sqlite:funcionarios.db";

    /** Abre a conexão garantindo que a tabela exista (mesmo se o .db for recriado). */
    public static Connection getConexao() throws SQLException {
        Connection c = DriverManager.getConnection(URL);
        try (Statement st = c.createStatement()) {
            st.execute(SQL_TABELA);
            migrar(c);
        } catch (SQLException e) {
            c.close();
            throw e;
        }
        return c;
    }

    /** Cria o banco e a tabela caso ainda não existam. */
    public static void inicializar() throws SQLException {
        getConexao().close();
    }

    /** Bancos criados antes da edição de bônus não têm a coluna bonus_percentual. */
    private static void migrar(Connection c) throws SQLException {
        boolean existe = false;
        try (Statement st = c.createStatement();
             java.sql.ResultSet rs = st.executeQuery("PRAGMA table_info(funcionario)")) {
            while (rs.next()) {
                if ("bonus_percentual".equals(rs.getString("name"))) existe = true;
            }
        }
        if (!existe) {
            try (Statement st = c.createStatement()) {
                st.execute("ALTER TABLE funcionario ADD COLUMN bonus_percentual REAL");
            }
        }
    }

    private static final String SQL_TABELA = montarSql();

    private static String montarSql() {
        return "CREATE TABLE IF NOT EXISTS funcionario ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "tipo TEXT NOT NULL,"          // 'GERENTE' ou 'CAIXA'
                + "nome TEXT NOT NULL,"
                + "cpf TEXT NOT NULL UNIQUE,"
                + "salario REAL NOT NULL,"
                + "departamento TEXT,"           // usado só por Gerente
                + "numero_caixa INTEGER,"        // usado só por Caixa
                + "bonus_percentual REAL)";      // null = padrão do cargo
    }
}
