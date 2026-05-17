package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxmvc.model.dao.FabricaParceiraDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.FabricaParceira;

public class FXMLCadastrosFabricaParceiraController implements Initializable {

    @FXML private TableView<FabricaParceira> tableViewFabricaParceira;
    @FXML private TableColumn<FabricaParceira, Integer> tableColumnFabricaParceiraId;
    @FXML private TableColumn<FabricaParceira, String> tableColumnFabricaParceiraName;
    @FXML private Button buttonInserir;
    @FXML private Button buttonAlterar;
    @FXML private Button buttonRemover;
    @FXML private Label labelNome;
    @FXML private Label labelCnpj;
    @FXML private Label labelTelefone;
    @FXML private Label labelEspecialidade;

    private List<FabricaParceira> listFabricas;
    private ObservableList<FabricaParceira> observableListFabricas;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final FabricaParceiraDAO dao = new FabricaParceiraDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao.setConnection(connection);
        carregarTableView();
        selecionarItemTableView(null);
        tableViewFabricaParceira.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableView(newValue));
    }

    public void carregarTableView() {
        tableColumnFabricaParceiraId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnFabricaParceiraName.setCellValueFactory(new PropertyValueFactory<>("nome"));
        listFabricas = dao.listar();
        observableListFabricas = FXCollections.observableArrayList(listFabricas);
        tableViewFabricaParceira.setItems(observableListFabricas);
    }

    public void selecionarItemTableView(FabricaParceira fabrica) {
        if (fabrica != null) {
            labelNome.setText(fabrica.getNome());
            labelCnpj.setText(fabrica.getCnpj());
            labelTelefone.setText(fabrica.getTelefone() != null ? fabrica.getTelefone() : "");
            labelEspecialidade.setText(fabrica.getEspecialidade() != null ? fabrica.getEspecialidade() : "");
        } else {
            labelNome.setText(""); labelCnpj.setText("");
            labelTelefone.setText(""); labelEspecialidade.setText("");
        }
    }

    @FXML
    public void handleButtonInserir() throws IOException {
        FabricaParceira fabrica = new FabricaParceira();
        if (showDialog(fabrica)) {
            dao.inserir(fabrica);
            carregarTableView();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        FabricaParceira fabrica = tableViewFabricaParceira.getSelectionModel().getSelectedItem();
        if (fabrica != null) {
            if (showDialog(fabrica)) {
                dao.alterar(fabrica);
                carregarTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma Fábrica na Tabela!");
            alert.show();
        }
    }

    @FXML
    public void handleButtonRemover() {
        FabricaParceira fabrica = tableViewFabricaParceira.getSelectionModel().getSelectedItem();
        if (fabrica != null) {
            dao.remover(fabrica);
            carregarTableView();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha uma Fábrica na Tabela!");
            alert.show();
        }
    }

    private boolean showDialog(FabricaParceira fabrica) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/javafxmvc/view/FXMLCadastrosFabricaParceiraDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Fábricas Parceiras");
        dialogStage.setScene(new Scene(page));
        FXMLCadastrosFabricaParceiraDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setFabricaParceira(fabrica);
        dialogStage.showAndWait();
        return controller.isButtonConfirmarClicked();
    }
}
