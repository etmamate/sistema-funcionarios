package model;

/** Gerente: possui departamento e recebe 20% de bônus sobre o salário. */
public class Gerente extends Funcionario {

    private String departamento;

    public Gerente(String nome, String cpf, double salario, String departamento) {
        super(nome, cpf, salario);
        this.departamento = departamento;
    }

    public Gerente(int id, String nome, String cpf, double salario, String departamento) {
        super(id, nome, cpf, salario);
        this.departamento = departamento;
    }

    @Override
    protected double getPercentualPadrao() {
        return 0.20;
    }

    @Override
    public void setInfoExtra(String valor) {
        this.departamento = valor;
    }

    @Override
    public String getCargo() {
        return "Gerente";
    }

    @Override
    public String getInfoExtra() {
        return "Depto: " + departamento;
    }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
