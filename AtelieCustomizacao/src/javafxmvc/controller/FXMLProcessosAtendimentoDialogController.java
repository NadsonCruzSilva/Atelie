package javafxmvc.controller;

import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.dao.ClienteDAO;
import javafxmvc.model.dao.OrdemServicoDAO;
import javafxmvc.model.dao.TipoServicoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Cliente;
import javafxmvc.model.domain.OrdemServico;
import javafxmvc.model.domain.TipoServico;

public class FXMLProcessosAtendimentoDialogController implements Initializable {

    @FXML
    private ComboBox<Cliente> comboBoxCliente;
    @FXML
    private ComboBox<TipoServico> comboBoxTipoServico;
    @FXML
    private DatePicker datePickerAbertura;
    @FXML
    private TextField textFieldValor;
    @FXML
    private Button buttonConfirmar;
    @FXML
    private Button buttonCancelar;

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private OrdemServico ordemServico;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoServicoDAO tipoServicoDAO = new TipoServicoDAO();
    private final OrdemServicoDAO ordemServicoDAO = new OrdemServicoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clienteDAO.setConnection(connection);
        tipoServicoDAO.setConnection(connection);
        ordemServicoDAO.setConnection(connection);

        List<Cliente> clientes = clienteDAO.listar();
        comboBoxCliente.setItems(FXCollections.observableArrayList(clientes));

        List<TipoServico> tipos = tipoServicoDAO.listar();
        comboBoxTipoServico.setItems(FXCollections.observableArrayList(tipos));

        datePickerAbertura.setValue(LocalDate.now());

        // Auto-calcular data prevista ao selecionar tipo de serviço
        comboBoxTipoServico.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && textFieldValor.getText().isEmpty()) {
                textFieldValor.setText(String.format("%.2f", newVal.getValor()));
            }
        });
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isButtonConfirmarClicked() {
        return buttonConfirmarClicked;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico os) {
        this.ordemServico = os;
        if (os.getCliente() != null)
            comboBoxCliente.setValue(os.getCliente());
        if (os.getDataAbertura() != null)
            datePickerAbertura.setValue(os.getDataAbertura());
        textFieldValor.setText(os.getValorTotal() > 0 ? String.format("%.2f", os.getValorTotal()) : "");
    }

    @FXML
    public void handleButtonConfirmar() {
        Cliente clienteSelecionado = comboBoxCliente.getValue();
        TipoServico tipoSelecionado = comboBoxTipoServico.getValue();

        if (clienteSelecionado == null) {
            showError("Selecione um Cliente!");
            return;
        }

        // REGRA DE NEGÓCIO 1: Bloqueio Inadimplente
        if (ordemServicoDAO.isClienteInadimplente(clienteSelecionado)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Operação Bloqueada");
            alert.setHeaderText("Cliente Inadimplente!");
            alert.setContentText("O cliente \"" + clienteSelecionado.getNome()
                    + "\" possui pendências ou está com mais de 30 dias de atraso. Não é possível abrir nova Ordem de Serviço.");
            alert.showAndWait();
            return;
        }

        if (tipoSelecionado == null) {
            showError("Selecione um Tipo de Serviço!");
            return;
        }
        if (datePickerAbertura.getValue() == null) {
            showError("Informe a Data de Abertura!");
            return;
        }
        
        if (!textFieldValor.getText().isEmpty() && !javafxmvc.utils.ValidadorUtil.isValorPositivo(textFieldValor.getText())) {
            showError("Valor total inválido! Use números maiores que zero (ex: 50.00)");
            return;
        }

        try {
            ordemServico.setCliente(clienteSelecionado);
            ordemServico.setDataAbertura(datePickerAbertura.getValue());
            
            // REGRA DE NEGÓCIO 3: Prazo Automático (Estático)
            ordemServico.setDataPrevista(datePickerAbertura.getValue().plusDays(tipoSelecionado.getPrazoEstimadoDias()));
            ordemServico.setStatus("RECEBIDA");
            
            double valorTotal = textFieldValor.getText().isEmpty() ? tipoSelecionado.getValor()
                    : Double.parseDouble(textFieldValor.getText().replace(",", "."));
            
            // NOVO: Regra de Negócio (Programa de Fidelidade)
            // Aplicamos o desconto apenas na criação de uma nova OS (id == 0), 
            // evitando descontos cumulativos ou sobrescrita em caso de edição (Alterar).
            if (ordemServico.getId() == 0) {
                int comprasAnteriores = ordemServicoDAO.getQuantidadeOSFinalizadas(clienteSelecionado);
                if (comprasAnteriores >= 3) {
                    // Aplica 10% de desconto
                    valorTotal = valorTotal * 0.90;
                }
            }
            
            ordemServico.setValorTotal(valorTotal);

            // Cria automaticamente o primeiro Encaminhamento (Item da OS)
            // vinculado ao TipoServico selecionado no Atendimento
            javafxmvc.model.domain.Encaminhamento enc = new javafxmvc.model.domain.Encaminhamento();
            enc.setTipoServico(tipoSelecionado);
            javafxmvc.model.domain.FabricaParceira fp = new javafxmvc.model.domain.FabricaParceira();
            fp.setId(1); // Setando uma fábrica padrão para evitar NOT NULL constraint
            enc.setFabricaParceira(fp);
            enc.setQuantidade(1);
            enc.setValorServico(valorTotal);
            enc.setDataEncaminhamento(ordemServico.getDataAbertura());
            enc.setDataRetornoPrevista(ordemServico.getDataPrevista());
            ordemServico.getEncaminhamentos().clear();
            ordemServico.getEncaminhamentos().add(enc);

            buttonConfirmarClicked = true;
            dialogStage.close();
        } catch (NumberFormatException e) {
            showError("Valor total inválido! Use números (ex: 50.00)");
        }
    }

    @FXML
    public void handleButtonCancelar() {
        dialogStage.close();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Campos inválidos");
        alert.setContentText(msg);
        alert.show();
    }
}
