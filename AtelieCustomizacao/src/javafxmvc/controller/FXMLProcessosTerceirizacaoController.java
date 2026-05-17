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
import javafxmvc.model.dao.EncaminhamentoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Encaminhamento;

public class FXMLProcessosTerceirizacaoController implements Initializable {

    @FXML private TableView<Encaminhamento> tableViewEncaminhamento;
    @FXML private TableColumn<Encaminhamento, Integer> tableColumnEncId;
    @FXML private TableColumn<Encaminhamento, String> tableColumnEncFabrica;
    @FXML private Button buttonInserir;
    @FXML private Button buttonRemover;

    private List<Encaminhamento> listEnc;
    private ObservableList<Encaminhamento> observableListEnc;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final EncaminhamentoDAO dao = new EncaminhamentoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao.setConnection(connection);
        carregarTableView();
    }

    public void carregarTableView() {
        tableColumnEncId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnEncFabrica.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFabricaParceira() != null)
                return new SimpleStringProperty(cellData.getValue().getFabricaParceira().getNome());
            return new SimpleStringProperty("");
        });
        listEnc = dao.listar();
        observableListEnc = FXCollections.observableArrayList(listEnc);
        tableViewEncaminhamento.setItems(observableListEnc);
    }

    @FXML
    public void handleButtonInserir() throws IOException {
        Encaminhamento enc = new Encaminhamento();
        if (showDialog(enc)) {
            carregarTableView();
        }
    }

    @FXML
    public void handleButtonRemover() {
        Encaminhamento selected = tableViewEncaminhamento.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dao.remover(selected);
            carregarTableView();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um Encaminhamento na Tabela!");
            alert.show();
        }
    }

    private boolean showDialog(Encaminhamento enc) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/javafxmvc/view/FXMLProcessosTerceirizacaoDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Terceirização - Encaminhar para Fábrica");
        dialogStage.setScene(new Scene(page));
        FXMLProcessosTerceirizacaoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setEncaminhamento(enc);
        dialogStage.showAndWait();
        return controller.isButtonConfirmarClicked();
    }
}
