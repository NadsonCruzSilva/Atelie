package javafxmvc.model.domain;

import java.io.Serializable;

public class TipoServico implements Serializable {
    private int id;
    private String descricao;
    private double valor;
    private int prazoEstimadoDias;

    public TipoServico() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public int getPrazoEstimadoDias() { return prazoEstimadoDias; }
    public void setPrazoEstimadoDias(int prazoEstimadoDias) { this.prazoEstimadoDias = prazoEstimadoDias; }

    @Override
    public String toString() { return this.descricao; }
}
