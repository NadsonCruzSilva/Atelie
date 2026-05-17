package javafxmvc.model.domain;

import java.io.Serializable;

public class FabricaParceira implements Serializable {
    private int id;
    private String nome;
    private String cnpj;
    private String telefone;
    private String especialidade;

    public FabricaParceira() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    @Override
    public String toString() { return this.nome; }
}
