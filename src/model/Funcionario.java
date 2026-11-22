package model;

/**
 * Superclasse abstrata com os atributos comuns a todos os funcionários.
 * Cada subclasse define o percentual de bônus padrão do seu cargo
 * (Polimorfismo), mas o percentual pode ser personalizado por funcionário.
 */
public abstract class Funcionario {

    private int id;
    private String nome;
    private String cpf;
    private double salario;
    private Double percentualPersonalizado; // null = usa o padrão do cargo

    public Funcionario(String nome, String cpf, double salario) {
        this(0, nome, cpf, salario);
    }

    public Funcionario(int id, String nome, String cpf, double salario) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.salario = salario;
    }

    /** Percentual de bônus padrão do cargo (ex.: 0.20 = 20%). */
    protected abstract double getPercentualPadrao();

    /** Nome do cargo, usado na listagem e na persistência. */
    public abstract String getCargo();

    /** Informação específica do cargo (departamento, nº do caixa...). */
    public abstract String getInfoExtra();

    /** Percentual efetivo: o personalizado, se houver, senão o padrão do cargo. */
    public double getPercentualBonus() {
        return percentualPersonalizado != null ? percentualPersonalizado : getPercentualPadrao();
    }

    /** Define um percentual personalizado (0.15 = 15%); null volta ao padrão do cargo. */
    public void setPercentualBonus(Double percentual) {
        this.percentualPersonalizado = percentual;
    }

    /** Percentual personalizado (null se estiver usando o padrão). */
    public Double getPercentualPersonalizado() {
        return percentualPersonalizado;
    }

    /** Bonificação = salário x percentual efetivo. */
    public double calcularBonificacao() {
        return salario * getPercentualBonus();
    }

    /** Salário final = salário base + bonificação. */
    public double calcularSalarioFinal() {
        return salario + calcularBonificacao();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    /** Altera o dado específico do cargo (departamento ou nº do caixa) a partir de um texto. */
    public abstract void setInfoExtra(String valor);

    @Override
    public String toString() {
        return String.format("%-4d %-10s %-20s %-14s R$ %9.2f  Bônus: R$ %8.2f  Final: R$ %9.2f  [%s]",
                id, getCargo(), nome, cpf, salario,
                calcularBonificacao(), calcularSalarioFinal(), getInfoExtra());
    }
}
