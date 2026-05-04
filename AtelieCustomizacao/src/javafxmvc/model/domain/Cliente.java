package javafxmvc.model.domain;

import java.io.Serializable;

public class Cliente implements Serializable {
    private int id;
    private String nome;
    private String cpf;
    private String telefone;
    private String endereco;
    private int diasAtraso;
    private boolean possuiPendencia;

    public Cliente() {}

    public Cliente(int id, String nome, String cpf) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public int getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(int diasAtraso) { this.diasAtraso = diasAtraso; }
    public boolean isPossuiPendencia() { return possuiPendencia; }
    public void setPossuiPendencia(boolean possuiPendencia) { this.possuiPendencia = possuiPendencia; }

    @Override
    public String toString() { return this.nome; }
}
