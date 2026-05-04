package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
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

public class FXMLProcessosAtendimentoController implements Initializable {

    @FXML private TableView<OrdemServico> tableViewOS;
    @FXML private TableColumn<OrdemServico, Integer> tableColumnOSId;
    @FXML private TableColumn<OrdemServico, String> tableColumnOSCliente;
    @FXML private TableColumn<OrdemServico, String> tableColumnOSStatus;
    @FXML private Button buttonInserir;
    @FXML private Button buttonAlterar;
    @FXML private Button buttonRemover;

    private List<OrdemServico> listOS;
    private ObservableList<OrdemServico> observableListOS;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final OrdemServicoDAO dao = new OrdemServicoDAO();

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
    public void handleButtonInserir() throws IOException {
        OrdemServico os = new OrdemServico();
        if (showDialog(os)) {
            dao.inserir(os);
            carregarTableView();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        OrdemServico os = tableViewOS.getSelectionModel().getSelectedItem();
        if (os != null) {
            if (showDialog(os)) {
                dao.alterar(os);
                carregarTableView();
            }
        } else {
            showAlert("Por favor, escolha uma Ordem de Serviço na Tabela!");
        }
    }

    @FXML
    public void handleButtonRemover() {
        OrdemServico os = tableViewOS.getSelectionModel().getSelectedItem();
        if (os != null) {
            dao.remover(os);
            carregarTableView();
        } else {
            showAlert("Por favor, escolha uma Ordem de Serviço na Tabela!");
        }
    }

    private boolean showDialog(OrdemServico os) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/javafxmvc/view/FXMLProcessosAtendimentoDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Atendimento - Ordem de Serviço");
        dialogStage.setScene(new Scene(page));
        FXMLProcessosAtendimentoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setOrdemServico(os);
        dialogStage.showAndWait();
        return controller.isButtonConfirmarClicked();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
