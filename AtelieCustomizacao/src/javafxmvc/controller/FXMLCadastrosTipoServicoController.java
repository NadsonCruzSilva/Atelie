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
import javafxmvc.model.dao.TipoServicoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.TipoServico;

public class FXMLCadastrosTipoServicoController implements Initializable {

    @FXML private TableView<TipoServico> tableViewTipoServico;
    @FXML private TableColumn<TipoServico, Integer> tableColumnTipoServicoId;
    @FXML private TableColumn<TipoServico, String> tableColumnTipoServicoName;
    @FXML private Button buttonInserir;
    @FXML private Button buttonAlterar;
    @FXML private Button buttonRemover;
    @FXML private Label labelDescricao;
    @FXML private Label labelValor;
    @FXML private Label labelPrazo;

    private List<TipoServico> listTipos;
    private ObservableList<TipoServico> observableListTipos;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final TipoServicoDAO dao = new TipoServicoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao.setConnection(connection);
        carregarTableView();
        selecionarItemTableView(null);
        tableViewTipoServico.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableView(newValue));
    }

    public void carregarTableView() {
        tableColumnTipoServicoId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnTipoServicoName.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        listTipos = dao.listar();
        observableListTipos = FXCollections.observableArrayList(listTipos);
        tableViewTipoServico.setItems(observableListTipos);
    }

    public void selecionarItemTableView(TipoServico ts) {
        if (ts != null) {
            labelDescricao.setText(ts.getDescricao());
            labelValor.setText(String.format("R$ %.2f", ts.getValor()));
            labelPrazo.setText(ts.getPrazoEstimadoDias() + " dias");
        } else {
            labelDescricao.setText("");
            labelValor.setText("");
            labelPrazo.setText("");
        }
    }

    @FXML
    public void handleButtonInserir() throws IOException {
        TipoServico ts = new TipoServico();
        if (showDialog(ts)) {
            dao.inserir(ts);
            carregarTableView();
        }
    }

    @FXML
    public void handleButtonAlterar() throws IOException {
        TipoServico ts = tableViewTipoServico.getSelectionModel().getSelectedItem();
        if (ts != null) {
            if (showDialog(ts)) {
                dao.alterar(ts);
                carregarTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um Tipo de Serviço na Tabela!");
            alert.show();
        }
    }

    @FXML
    public void handleButtonRemover() {
        TipoServico ts = tableViewTipoServico.getSelectionModel().getSelectedItem();
        if (ts != null) {
            dao.remover(ts);
            carregarTableView();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um Tipo de Serviço na Tabela!");
            alert.show();
        }
    }

    private boolean showDialog(TipoServico ts) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/javafxmvc/view/FXMLCadastrosTipoServicoDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Tipos de Serviço");
        dialogStage.setScene(new Scene(page));
        FXMLCadastrosTipoServicoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setTipoServico(ts);
        dialogStage.showAndWait();
        return controller.isButtonConfirmarClicked();
    }
}
