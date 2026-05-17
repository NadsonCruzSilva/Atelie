package javafxmvc.model.domain;

import java.io.Serializable;

public class ReceitaServico implements Serializable {
    private String descricao;
    private double valorUnitario;
    private long quantidade;
    private double receitaTotal;

    public ReceitaServico() {}

    public ReceitaServico(String descricao, double valorUnitario, long quantidade, double receitaTotal) {
        this.descricao = descricao;
        this.valorUnitario = valorUnitario;
        this.quantidade = quantidade;
        this.receitaTotal = receitaTotal;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(double valorUnitario) { this.valorUnitario = valorUnitario; }

    public long getQuantidade() { return quantidade; }
    public void setQuantidade(long quantidade) { this.quantidade = quantidade; }

    public double getReceitaTotal() { return receitaTotal; }
    public void setReceitaTotal(double receitaTotal) { this.receitaTotal = receitaTotal; }
}
