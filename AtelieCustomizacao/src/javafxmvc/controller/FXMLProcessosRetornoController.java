package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxmvc.model.dao.OrdemServicoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.OrdemServico;

public class FXMLProcessosRetornoController implements Initializable {

    @FXML private TableView<OrdemServico>               tableViewOS;
    @FXML private TableColumn<OrdemServico, Integer>    tableColumnOSId;
    @FXML private TableColumn<OrdemServico, String>     tableColumnOSCliente;
    @FXML private TableColumn<OrdemServico, String>     tableColumnOSStatus;
    @FXML private Button                                buttonFinalizar;

    private List<OrdemServico>           listOS;
    private ObservableList<OrdemServico> observableListOS;

    private final Database        database   = DatabaseFactory.getDatabase("postgresql");
    private final Connection      connection = database.conectar();
    private final OrdemServicoDAO dao        = new OrdemServicoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao.setConnection(connection);
        carregarTableView();
    }

    public void carregarTableView() {
        tableColumnOSId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnOSCliente.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCliente() != null)
                return new SimpleStringProperty(cellData.getValue().getCliente().getNome());
            return new SimpleStringProperty("");
        });
        tableColumnOSStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        listOS = dao.listar();
        observableListOS = FXCollections.observableArrayList(listOS);
        tableViewOS.setItems(observableListOS);
    }

    @FXML
    public void handleButtonFinalizar() {
        OrdemServico selected = tableViewOS.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma OS selecionada",
                    "Por favor, selecione uma Ordem de Serviço na tabela.");
            return;
        }
        if ("FINALIZADA".equals(selected.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "OS já finalizada",
                    "Esta Ordem de Serviço já está com status FINALIZADA.");
            return;
        }

        // Abre o dialog de confirmação exibindo os detalhes da OS
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/javafxmvc/view/FXMLProcessosRetornoDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Retorno e Pagamento — OS #" + selected.getId());
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(page));

            FXMLProcessosRetornoDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setOrdemServico(selected);

            dialogStage.showAndWait();

            if (controller.isButtonConfirmarClicked()) {
                finalizarOS(selected, controller.getValorFinalInformado());
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao abrir o formulário de confirmação:\n" + e.getMessage());
        }
    }

    /**
     * Executa a transação de finalização da OS.
     * Atualiza status → FINALIZADA, registra data de retorno e ajusta valor total.
     *
     * Regra de Negócio (Prazo Automático): a data prevista já foi calculada
     * automaticamente no momento do encaminhamento (dataEncaminhamento + prazo do TipoServico).
     * Aqui apenas registramos a data real de retorno.
     */
    private void finalizarOS(OrdemServico os, double valorFinalInformado) {
        try {
            connection.setAutoCommit(false);

            os.setStatus("FINALIZADA");
            os.setDataRetorno(LocalDate.now());

            // Aplica o valor final apenas se o usuário informou um valor diferente
            if (valorFinalInformado >= 0) {
                os.setValorTotal(valorFinalInformado);
            }

            dao.alterar(os);
            connection.commit();
            connection.setAutoCommit(true);

            showAlert(Alert.AlertType.INFORMATION, "Sucesso!",
                    "A OS #" + os.getId() + " foi finalizada com sucesso!\n" +
                    "Data de retorno: " + os.getDataRetorno() + "\n" +
                    "Valor total: R$ " + String.format("%.2f", os.getValorTotal()));

            carregarTableView();

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showAlert(Alert.AlertType.ERROR, "Erro ao Finalizar",
                    "Não foi possível finalizar a OS:\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String header, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Ateliê — Processo de Retorno");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
