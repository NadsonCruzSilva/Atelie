package javafxmvc.model.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdemServico implements Serializable {
    private int id;
    private Cliente cliente;
    private LocalDate dataAbertura;
    private LocalDate dataPrevista;
    private LocalDate dataRetorno;
    private String status;
    private double valorTotal;
    private List<Encaminhamento> encaminhamentos;

    public OrdemServico() {
        this.encaminhamentos = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDate getDataPrevista() { return dataPrevista; }
    public void setDataPrevista(LocalDate dataPrevista) { this.dataPrevista = dataPrevista; }
    public LocalDate getDataRetorno() { return dataRetorno; }
    public void setDataRetorno(LocalDate dataRetorno) { this.dataRetorno = dataRetorno; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    public List<Encaminhamento> getEncaminhamentos() { return encaminhamentos; }
    public void setEncaminhamentos(List<Encaminhamento> encaminhamentos) { this.encaminhamentos = encaminhamentos; }

    @Override
    public String toString() { return "OS #" + this.id + " - " + (this.cliente != null ? this.cliente.getNome() : ""); }
}
