package app;

import db.Conexao;
import db.FuncionarioDAO;
import model.Caixa;
import model.Funcionario;
import model.Gerente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

/** Interface gráfica (Swing) do Sistema de Gestão de Funcionários. */
public class TelaPrincipal extends JFrame {

    private final FuncionarioDAO dao = new FuncionarioDAO();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private final JComboBox<String> cbCargo = new JComboBox<>(new String[]{"Gerente", "Caixa"});
    private final JTextField txtNome = new JTextField(18);
    private final JTextField txtCpf = new JTextField(12);
    private final JTextField txtSalario = new JTextField(10);
    private final JLabel lblExtra = new JLabel("Departamento:");
    private final JTextField txtExtra = new JTextField(12);
    private final JTextField txtBonus = new JTextField(5);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Cargo", "Nome", "CPF", "Salário", "Bônus %", "Bônus", "Salário Final", "Detalhe"}, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final JLabel lblTotal = new JLabel("Total da folha: R$ 0,00");

    public TelaPrincipal() {
        super("Sistema de Gestão de Funcionários");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(montarFormulario(), BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(montarRodape(), BorderLayout.SOUTH);

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editar();
            }
        });

        cbCargo.addActionListener(e ->
                lblExtra.setText(cbCargo.getSelectedItem().equals("Gerente") ? "Departamento:" : "Nº do caixa:"));

        setSize(900, 500);
        setLocationRelativeTo(null);
        atualizarTabela();
    }

    private JPanel montarFormulario() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        p.setBorder(BorderFactory.createTitledBorder("Novo funcionário"));
        p.add(new JLabel("Cargo:"));
        p.add(cbCargo);
        p.add(new JLabel("Nome:"));
        p.add(txtNome);
        p.add(new JLabel("CPF:"));
        p.add(txtCpf);
        p.add(new JLabel("Salário:"));
        p.add(txtSalario);
        p.add(lblExtra);
        p.add(txtExtra);
        p.add(new JLabel("Bônus % (vazio = padrão):"));
        p.add(txtBonus);
        JButton btCadastrar = new JButton("Cadastrar");
        btCadastrar.addActionListener(e -> cadastrar());
        p.add(btCadastrar);
        return p;
    }

    private JPanel montarRodape() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btSalario = new JButton("Editar selecionado");
        JButton btExcluir = new JButton("Excluir selecionado");
        JButton btAtualizar = new JButton("Recarregar");
        btSalario.addActionListener(e -> editar());
        btExcluir.addActionListener(e -> excluir());
        btAtualizar.addActionListener(e -> atualizarTabela());
        botoes.add(btSalario);
        botoes.add(btExcluir);
        botoes.add(btAtualizar);
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        p.add(botoes, BorderLayout.WEST);
        p.add(lblTotal, BorderLayout.EAST);
        return p;
    }

    private void cadastrar() {
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String extra = txtExtra.getText().trim();
        if (nome.isEmpty() || cpf.isEmpty() || extra.isEmpty()) {
            erro("Preencha todos os campos.");
            return;
        }
        Double salario = lerValor(txtSalario.getText());
        if (salario == null) return;
        boolean bonusVazio = txtBonus.getText().trim().isEmpty();
        Double bonus = bonusVazio ? null : lerPercentual(txtBonus.getText());
        if (!bonusVazio && bonus == null) return;

        try {
            Funcionario f;
            if (cbCargo.getSelectedItem().equals("Gerente")) {
                f = new Gerente(nome, cpf, salario, extra);
            } else {
                f = new Caixa(nome, cpf, salario, Integer.parseInt(extra));
            }
            f.setPercentualBonus(bonus);
            dao.salvar(f);
            txtNome.setText("");
            txtCpf.setText("");
            txtSalario.setText("");
            txtExtra.setText("");
            txtBonus.setText("");
            atualizarTabela();
        } catch (NumberFormatException e) {
            erro("O número do caixa deve ser um inteiro.");
        } catch (SQLException e) {
            erro(e.getMessage().contains("UNIQUE") ? "Já existe um funcionário com esse CPF."
                    : "Erro no banco de dados: " + e.getMessage());
        }
    }

    /** Abre o diálogo de edição do funcionário selecionado (nome, CPF, salário, dado do cargo e bônus). */
    private void editar() {
        Integer id = idSelecionado();
        if (id == null) return;
        try {
            Funcionario f = dao.buscarPorId(id);
            if (f == null) {
                erro("Funcionário não encontrado.");
                atualizarTabela();
                return;
            }

            JTextField nome = new JTextField(f.getNome(), 20);
            JTextField cpf = new JTextField(f.getCpf(), 20);
            JTextField salario = new JTextField(String.format(Locale.of("pt", "BR"), "%.2f", f.getSalario()), 20);
            String rotuloExtra = f instanceof Gerente ? "Departamento:" : "Nº do caixa:";
            String valorExtra = f instanceof Gerente g ? g.getDepartamento()
                    : String.valueOf(((Caixa) f).getNumeroCaixa());
            JTextField extra = new JTextField(valorExtra, 20);
            JTextField bonus = new JTextField(formatarPercentual(f.getPercentualBonus() * 100), 20);

            JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
            p.add(new JLabel("Cargo:"));
            p.add(new JLabel(f.getCargo() + " (ID " + f.getId() + ")"));
            p.add(new JLabel("Nome:"));
            p.add(nome);
            p.add(new JLabel("CPF:"));
            p.add(cpf);
            p.add(new JLabel("Salário base:"));
            p.add(salario);
            p.add(new JLabel(rotuloExtra));
            p.add(extra);
            p.add(new JLabel("Bônus (%):"));
            p.add(bonus);

            while (true) {
                int r = JOptionPane.showConfirmDialog(this, p, "Editar funcionário",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (r != JOptionPane.OK_OPTION) return;

                if (nome.getText().trim().isEmpty() || cpf.getText().trim().isEmpty()
                        || extra.getText().trim().isEmpty()) {
                    erro("Preencha todos os campos.");
                    continue;
                }
                Double novoSalario = lerValor(salario.getText());
                if (novoSalario == null) continue;
                Double novoBonus = lerPercentual(bonus.getText());
                if (novoBonus == null) continue;
                try {
                    f.setInfoExtra(extra.getText().trim());
                } catch (NumberFormatException e) {
                    erro("O número do caixa deve ser um inteiro.");
                    continue;
                }
                f.setNome(nome.getText().trim());
                f.setCpf(cpf.getText().trim());
                f.setSalario(novoSalario);
                f.setPercentualBonus(novoBonus);
                try {
                    dao.atualizar(f);
                    break;
                } catch (SQLException e) {
                    erro(e.getMessage().contains("UNIQUE") ? "Já existe outro funcionário com esse CPF."
                            : "Erro no banco de dados: " + e.getMessage());
                }
            }
            atualizarTabela();
        } catch (SQLException e) {
            erro("Erro no banco de dados: " + e.getMessage());
        }
    }

    /** Lê um percentual digitado (ex.: 15 ou 12,5) e devolve em fração (0.15). */
    private Double lerPercentual(String texto) {
        try {
            double v = Double.parseDouble(texto.trim().replace(',', '.'));
            if (v < 0 || v > 100) throw new NumberFormatException();
            return v / 100.0;
        } catch (NumberFormatException e) {
            erro("Informe um bônus entre 0 e 100 (%), ex.: 15 ou 12,5.");
            return null;
        }
    }

    private String formatarPercentual(double v) {
        return (v == Math.rint(v) ? String.valueOf((long) v) : String.format(Locale.of("pt", "BR"), "%.2f", v));
    }

    private void excluir() {
        Integer id = idSelecionado();
        if (id == null) return;
        int r = JOptionPane.showConfirmDialog(this, "Excluir o funcionário ID " + id + "?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(id);
            atualizarTabela();
        } catch (SQLException e) {
            erro("Erro no banco de dados: " + e.getMessage());
        }
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);
        double total = 0;
        try {
            // Polimorfismo: cada Funcionario calcula o bônus pela regra do seu cargo
            for (Funcionario f : dao.listar()) {
                modelo.addRow(new Object[]{f.getId(), f.getCargo(), f.getNome(), f.getCpf(),
                        moeda.format(f.getSalario()), formatarPercentual(f.getPercentualBonus() * 100) + "%",
                        moeda.format(f.calcularBonificacao()),
                        moeda.format(f.calcularSalarioFinal()), f.getInfoExtra()});
                total += f.calcularSalarioFinal();
            }
        } catch (SQLException e) {
            erro("Erro ao listar: " + e.getMessage());
        }
        lblTotal.setText("Total da folha: " + moeda.format(total));
    }

    private Integer idSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            erro("Selecione um funcionário na tabela.");
            return null;
        }
        return (Integer) modelo.getValueAt(linha, 0);
    }

    private Double lerValor(String texto) {
        try {
            double v = Double.parseDouble(texto.trim().replace(',', '.'));
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            erro("Informe um salário numérico válido (ex.: 3500,50).");
            return null;
        }
    }

    private void erro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            Conexao.inicializar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao iniciar o banco: " + e.getMessage());
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new TelaPrincipal().setVisible(true);
        });
    }
}
