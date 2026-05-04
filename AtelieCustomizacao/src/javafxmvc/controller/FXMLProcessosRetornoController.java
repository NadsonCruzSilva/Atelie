package javafxmvc.controller;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxmvc.model.dao.OrdemServicoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.OrdemServico;

public class FXMLProcessosRetornoController implements Initializable {

    @FXML private TableView<OrdemServico> tableViewOS;
    @FXML private TableColumn<OrdemServico, Integer> tableColumnOSId;
    @FXML private TableColumn<OrdemServico, String> tableColumnOSCliente;
    @FXML private TableColumn<OrdemServico, String> tableColumnOSStatus;
    @FXML private TextField textFieldValorFinal;
    @FXML private Button buttonFinalizar;

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
    public void handleButtonFinalizar() {
        OrdemServico selected = tableViewOS.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Por favor, escolha uma Ordem de Serviço na Tabela!");
            return;
        }
        if ("FINALIZADA".equals(selected.getStatus())) {
            showAlert("Esta OS já está FINALIZADA!");
            return;
        }

        try {
            // Transação manual: atualizar status + registrar retorno
            connection.setAutoCommit(false);

            selected.setStatus("FINALIZADA");
            selected.setDataRetorno(LocalDate.now());

            // Atualiza valor final se preenchido
            if (textFieldValorFinal != null && !textFieldValorFinal.getText().isEmpty()) {
                try {
                    selected.setValorTotal(Double.parseDouble(textFieldValorFinal.getText().replace(",", ".")));
                } catch (NumberFormatException e) {
                    showAlert("Valor final inválido!");
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return;
                }
            }

            dao.alterar(selected);
            connection.commit();
            connection.setAutoCommit(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("OS Finalizada!");
            alert.setContentText("A Ordem de Serviço #" + selected.getId() + " foi finalizada com sucesso.");
            alert.showAndWait();

            carregarTableView();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro ao finalizar OS: " + e.getMessage());
            try { connection.rollback(); connection.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}
