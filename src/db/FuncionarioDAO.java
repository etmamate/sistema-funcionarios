package db;

import model.Caixa;
import model.Funcionario;
import model.Gerente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acesso a dados (CRUD) da tabela funcionario usando JDBC puro. */
public class FuncionarioDAO {

    public void salvar(Funcionario f) throws SQLException {
        String sql = "INSERT INTO funcionario (tipo, nome, cpf, salario, departamento, numero_caixa, bonus_percentual) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getCargo().toUpperCase());
            ps.setString(2, f.getNome());
            ps.setString(3, f.getCpf());
            ps.setDouble(4, f.getSalario());
            if (f instanceof Gerente g) {
                ps.setString(5, g.getDepartamento());
                ps.setNull(6, java.sql.Types.INTEGER);
            } else if (f instanceof Caixa cx) {
                ps.setNull(5, java.sql.Types.VARCHAR);
                ps.setInt(6, cx.getNumeroCaixa());
            }
            if (f.getPercentualPersonalizado() != null) {
                ps.setDouble(7, f.getPercentualPersonalizado());
            } else {
                ps.setNull(7, java.sql.Types.REAL);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) f.setId(keys.getInt(1));
            }
        }
    }

    public List<Funcionario> listar() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection c = Conexao.getConexao();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM funcionario ORDER BY id")) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public Funcionario buscarPorId(int id) throws SQLException {
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM funcionario WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    /** Atualiza nome, CPF, salário, dado do cargo e percentual de bônus do funcionário. */
    public boolean atualizar(Funcionario f) throws SQLException {
        String sql = "UPDATE funcionario SET nome = ?, cpf = ?, salario = ?, "
                + "departamento = ?, numero_caixa = ?, bonus_percentual = ? WHERE id = ?";
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCpf());
            ps.setDouble(3, f.getSalario());
            if (f instanceof Gerente g) {
                ps.setString(4, g.getDepartamento());
                ps.setNull(5, java.sql.Types.INTEGER);
            } else if (f instanceof Caixa cx) {
                ps.setNull(4, java.sql.Types.VARCHAR);
                ps.setInt(5, cx.getNumeroCaixa());
            }
            if (f.getPercentualPersonalizado() != null) {
                ps.setDouble(6, f.getPercentualPersonalizado());
            } else {
                ps.setNull(6, java.sql.Types.REAL);
            }
            ps.setInt(7, f.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement("DELETE FROM funcionario WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Converte uma linha do banco no objeto da subclasse correta. */
    private Funcionario montar(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String cpf = rs.getString("cpf");
        double salario = rs.getDouble("salario");
        Funcionario f;
        if ("GERENTE".equals(rs.getString("tipo"))) {
            f = new Gerente(id, nome, cpf, salario, rs.getString("departamento"));
        } else {
            f = new Caixa(id, nome, cpf, salario, rs.getInt("numero_caixa"));
        }
        double bonus = rs.getDouble("bonus_percentual");
        if (!rs.wasNull()) {
            f.setPercentualBonus(bonus);
        }
        return f;
    }
}
