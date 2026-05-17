package javafxmvc.model.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class Encaminhamento implements Serializable {
    private int id;
    private OrdemServico ordemServico;
    private FabricaParceira fabricaParceira;
    private TipoServico tipoServico;
    private LocalDate dataEncaminhamento;
    private LocalDate dataRetornoPrevista;
    private int quantidade;
    private double valorServico;

    public Encaminhamento() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public OrdemServico getOrdemServico() { return ordemServico; }
    public void setOrdemServico(OrdemServico ordemServico) { this.ordemServico = ordemServico; }
    public FabricaParceira getFabricaParceira() { return fabricaParceira; }
    public void setFabricaParceira(FabricaParceira fabricaParceira) { this.fabricaParceira = fabricaParceira; }
    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) { this.tipoServico = tipoServico; }
    public LocalDate getDataEncaminhamento() { return dataEncaminhamento; }
    public void setDataEncaminhamento(LocalDate dataEncaminhamento) { this.dataEncaminhamento = dataEncaminhamento; }
    public LocalDate getDataRetornoPrevista() { return dataRetornoPrevista; }
    public void setDataRetornoPrevista(LocalDate dataRetornoPrevista) { this.dataRetornoPrevista = dataRetornoPrevista; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public double getValorServico() { return valorServico; }
    public void setValorServico(double valorServico) { this.valorServico = valorServico; }
    
    @Override
    public String toString() { return "Encaminhamento #" + this.id; }
}
