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
import javafxmvc.model.dao.ClienteDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.Cliente;

public class FXMLCadastrosClienteController implements Initializable {

    @FXML private TableView<Cliente> tableViewCliente;
    @FXML private TableColumn<Cliente, Integer> tableColumnClienteId;
    @FXML private TableColumn<Cliente, String> tableColumnClienteName;
    @FXML private Button buttonInserir;
    @FXML private Button buttonAlterar;
    @FXML private Button buttonRemover;
    @FXML private Label labelNome;
    @FXML private Label labelCpf;
    @FXML private Label labelTelefone;
    @FXML private Label labelEndereco;

    private List<Cliente> listClientes;
    private ObservableList<Cliente> observableListClientes;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final ClienteDAO dao = new ClienteDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao.setConnection(connection);
        carregarTableView();

        selecionarItemTableView(null);
        tableViewCliente.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableView(newValue));
    }

    public void carregarTableView() {
        tableColumnClienteId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnClienteName.setCellValueFactory(new PropertyValueFactory<>("nome"));
        listClientes = dao.listar();
        observableListClientes = FXCollections.observableArrayList(listClientes);
        tableViewCliente.setItems(observableListClientes);
    }

    public void selecionarItemTableView(Cliente cliente) {
        if (cliente != null) {
            labelNome.setText(cliente.getNome());
            labelCpf.setText(cliente.getCpf());
            labelTelefone.setText(cliente.getTelefone() != null ? cliente.getTelefone() : "");
            labelEndereco.setText(cliente.getEndereco() != null ? cliente.getEndereco() : "");
        } else {
            labelNome.setText("");
            labelCpf.setText("");
            labelTelefone.setText("");
            labelEndereco.setText("");
        }
    }

    @FXML
    public void handleButtonInserir() throws IOException {
        Cliente cliente = new Cliente();
        boolean confirmado = showDialog(cliente);
        if (confirmado) {
            dao.inserir(cliente);
            carregarTableView();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        Cliente cliente = tableViewCliente.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            boolean confirmado = showDialog(cliente);
            if (confirmado) {
                dao.alterar(cliente);
                carregarTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um Cliente na Tabela!");
            alert.show();
        }
    }

    @FXML
    public void handleButtonRemover() {
        Cliente cliente = tableViewCliente.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            dao.remover(cliente);
            carregarTableView();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um Cliente na Tabela!");
            alert.show();
        }
    }

    private boolean showDialog(Cliente cliente) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/javafxmvc/view/FXMLCadastrosClienteDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Clientes");
        dialogStage.setScene(new Scene(page));

        FXMLCadastrosClienteDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCliente(cliente);

        dialogStage.showAndWait();
        return controller.isButtonConfirmarClicked();
    }
}
