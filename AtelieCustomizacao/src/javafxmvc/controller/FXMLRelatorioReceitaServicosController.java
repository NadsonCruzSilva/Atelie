package javafxmvc.controller;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;
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
import javafxmvc.model.dao.TipoServicoDAO;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import javafxmvc.model.domain.ReceitaServico;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class FXMLRelatorioReceitaServicosController implements Initializable {

    @FXML private TableView<ReceitaServico> tableViewReceita;
    @FXML private TableColumn<ReceitaServico, String> tableColumnDescricao;
    @FXML private TableColumn<ReceitaServico, Double> tableColumnValorUnitario;
    @FXML private TableColumn<ReceitaServico, Long> tableColumnQuantidade;
    @FXML private TableColumn<ReceitaServico, Double> tableColumnReceitaTotal;
    @FXML private TextField textFieldFiltro;
    @FXML private Button buttonFiltrar;
    @FXML private Button buttonImprimir;

    private List<ReceitaServico> listReceita;
    private ObservableList<ReceitaServico> observableListReceita;

    private final Database database = DatabaseFactory.getDatabase("postgresql");
    private final Connection connection = database.conectar();
    private final TipoServicoDAO tipoServicoDAO = new TipoServicoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tipoServicoDAO.setConnection(connection);
        carregarTableView();
    }

    public void carregarTableView() {
        tableColumnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        tableColumnValorUnitario.setCellValueFactory(new PropertyValueFactory<>("valorUnitario"));
        tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        tableColumnReceitaTotal.setCellValueFactory(new PropertyValueFactory<>("receitaTotal"));

        String filtro = textFieldFiltro.getText() != null && !textFieldFiltro.getText().isEmpty() 
                        ? "%" + textFieldFiltro.getText() + "%" : "%";

        listReceita = tipoServicoDAO.getReceitasServicos(filtro);
        observableListReceita = FXCollections.observableArrayList(listReceita);
        tableViewReceita.setItems(observableListReceita);
    }

    @FXML
    public void handleButtonFiltrar() {
        carregarTableView();
    }

    @FXML
    public void handleButtonImprimir() {
        String filtro = textFieldFiltro.getText() != null && !textFieldFiltro.getText().isEmpty() 
                        ? "%" + textFieldFiltro.getText() + "%" : "%";
                        
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("tipo_servico", filtro);

        try {
            URL url = getClass().getResource("/javafxmvc/relatorios/RelatorioReceitaServicos.jrxml");
            if (url == null) {
                mostrarErroRelatorio("Arquivo de relatório não encontrado!");
                return;
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(url.openStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle("Receita de Serviços — Ateliê de Customização");
            jasperViewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErroRelatorio("Erro ao gerar o relatório:\n" + e.getMessage());
        }
    }
    
    private void mostrarErroRelatorio(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no Relatório");
        alert.setHeaderText("Não foi possível gerar o relatório.");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
