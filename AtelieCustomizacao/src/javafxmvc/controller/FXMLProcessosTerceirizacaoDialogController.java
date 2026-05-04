package javafxmvc.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
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
import javafxmvc.model.dao.EncaminhamentoDAO;
import javafxmvc.model.dao.FabricaParceiraDAO;
import javafxmvc.model.dao.OrdemServicoDAO;
import javafxmvc.model.dao.TipoServicoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Encaminhamento;
import javafxmvc.model.domain.FabricaParceira;
import javafxmvc.model.domain.OrdemServico;
import javafxmvc.model.domain.TipoServico;

public class FXMLProcessosTerceirizacaoDialogController implements Initializable {

    @FXML private ComboBox<OrdemServico> comboBoxOS;
    @FXML private ComboBox<FabricaParceira> comboBoxFabrica;
    @FXML private ComboBox<TipoServico> comboBoxTipoServico;
    @FXML private TextField textFieldQuantidade;
    @FXML private DatePicker datePickerEnvio;
    @FXML private Button buttonConfirmar;
    @FXML private Button buttonCancelar;

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Encaminhamento encaminhamento;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final OrdemServicoDAO osDAO = new OrdemServicoDAO();
    private final FabricaParceiraDAO fabricaDAO = new FabricaParceiraDAO();
    private final TipoServicoDAO tipoDAO = new TipoServicoDAO();
    private final EncaminhamentoDAO encDAO = new EncaminhamentoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        osDAO.setConnection(connection);
        fabricaDAO.setConnection(connection);
        tipoDAO.setConnection(connection);
        encDAO.setConnection(connection);

        List<OrdemServico> osList = osDAO.listar();
        comboBoxOS.setItems(FXCollections.observableArrayList(osList));

        List<FabricaParceira> fabricas = fabricaDAO.listar();
        comboBoxFabrica.setItems(FXCollections.observableArrayList(fabricas));

        List<TipoServico> tipos = tipoDAO.listar();
        comboBoxTipoServico.setItems(FXCollections.observableArrayList(tipos));

        datePickerEnvio.setValue(LocalDate.now());
    }

    public Stage getDialogStage() { return dialogStage; }
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }
    public boolean isButtonConfirmarClicked() { return buttonConfirmarClicked; }

    public Encaminhamento getEncaminhamento() { return encaminhamento; }
    public void setEncaminhamento(Encaminhamento enc) { this.encaminhamento = enc; }

    @FXML
    public void handleButtonConfirmar() {
        OrdemServico osSelecionada = comboBoxOS.getValue();
        FabricaParceira fabricaSelecionada = comboBoxFabrica.getValue();
        TipoServico tipoSelecionado = comboBoxTipoServico.getValue();

        if (osSelecionada == null) { showError("Selecione uma Ordem de Serviço!"); return; }
        if (fabricaSelecionada == null) { showError("Selecione uma Fábrica Parceira!"); return; }
        if (tipoSelecionado == null) { showError("Selecione um Tipo de Serviço!"); return; }
        if (textFieldQuantidade.getText().isEmpty()) { showError("Informe a Quantidade!"); return; }

        // REGRA DE NEGÓCIO 2: Limite Operacional da Fábrica
        if (encDAO.isLimiteOperacionalExcedido(fabricaSelecionada)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Operação Bloqueada");
            alert.setHeaderText("Limite Operacional Atingido!");
            alert.setContentText("A fábrica \"" + fabricaSelecionada.getNome() + "\" já possui 10 ou mais ordens ativas. Não é possível enviar mais peças.");
            alert.showAndWait();
            return;
        }

        try {
            // Transação manual: alterar OS + inserir encaminhamento atomicamente
            connection.setAutoCommit(false);

            encaminhamento.setOrdemServico(osSelecionada);
            encaminhamento.setFabricaParceira(fabricaSelecionada);
            encaminhamento.setTipoServico(tipoSelecionado);
            encaminhamento.setDataEncaminhamento(datePickerEnvio.getValue());
            // REGRA DE NEGÓCIO 3: Prazo Automático
            encaminhamento.setDataRetornoPrevista(datePickerEnvio.getValue().plusDays(tipoSelecionado.getPrazoEstimadoDias()));
            encaminhamento.setQuantidade(Integer.parseInt(textFieldQuantidade.getText()));
            encaminhamento.setValorServico(tipoSelecionado.getValor() * encaminhamento.getQuantidade());

            encDAO.inserir(encaminhamento);

            // Atualizar status da OS
            osSelecionada.setStatus("ENVIADA_FABRICA");
            osDAO.alterar(osSelecionada);

            connection.commit();
            connection.setAutoCommit(true);

            buttonConfirmarClicked = true;
            dialogStage.close();

        } catch (NumberFormatException e) {
            showError("Quantidade inválida!");
            rollback();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erro de banco de dados: " + e.getMessage());
            rollback();
        }
    }

    private void rollback() {
        try { connection.rollback(); connection.setAutoCommit(true); }
        catch (SQLException ex) { ex.printStackTrace(); }
    }

    @FXML
    public void handleButtonCancelar() { dialogStage.close(); }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro"); alert.setHeaderText("Operação inválida");
        alert.setContentText(msg); alert.show();
    }
}
