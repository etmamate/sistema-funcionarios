package model;

/** Caixa: possui número do caixa e recebe 10% de bônus sobre o salário. */
public class Caixa extends Funcionario {

    private int numeroCaixa;

    public Caixa(String nome, String cpf, double salario, int numeroCaixa) {
        super(nome, cpf, salario);
        this.numeroCaixa = numeroCaixa;
    }

    public Caixa(int id, String nome, String cpf, double salario, int numeroCaixa) {
        super(id, nome, cpf, salario);
        this.numeroCaixa = numeroCaixa;
    }

    @Override
    protected double getPercentualPadrao() {
        return 0.10;
    }

    @Override
    public void setInfoExtra(String valor) {
        this.numeroCaixa = Integer.parseInt(valor.trim());
    }

    @Override
    public String getCargo() {
        return "Caixa";
    }

    @Override
    public String getInfoExtra() {
        return "Caixa nº " + numeroCaixa;
    }

    public int getNumeroCaixa() { return numeroCaixa; }
    public void setNumeroCaixa(int numeroCaixa) { this.numeroCaixa = numeroCaixa; }
}
