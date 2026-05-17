package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafxmvc.model.database.Database;
import javafxmvc.model.database.DatabaseFactory;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.control.TextInputDialog;

public class FXMLVBoxMainController implements Initializable {

    @FXML private MenuItem menuItemCadastrosClientes;
    @FXML private MenuItem menuItemCadastrosFabricaParceira;
    @FXML private MenuItem menuItemCadastrosTipoServico;
    @FXML private MenuItem menuItemProcessosAtendimento;
    @FXML private MenuItem menuItemProcessosTerceirizacao;
    @FXML private MenuItem menuItemProcessosRetorno;
    @FXML private MenuItem menuItemGraficosStatus;
    @FXML private MenuItem menuItemGraficosVolume;
    @FXML private MenuItem menuItemGraficosReceita;
    @FXML private MenuItem menuItemRelatoriosOrdensAtraso;
    @FXML private MenuItem menuItemRelatoriosRankingFabricas;
    @FXML private MenuItem menuItemRelatoriosReceitaServicos;
    @FXML private MenuItem menuItemSair;
    @FXML private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    // =========================================================
    // NAVEGAÇÃO — carrega telas filhas dentro do AnchorPane
    // =========================================================
    private void loadView(String fxmlPath) {
        try {
            AnchorPane a = (AnchorPane) FXMLLoader.load(getClass().getResource(fxmlPath));
            anchorPane.getChildren().setAll(a);
            AnchorPane.setTopAnchor(a, 0.0);
            AnchorPane.setBottomAnchor(a, 0.0);
            AnchorPane.setLeftAnchor(a, 0.0);
            AnchorPane.setRightAnchor(a, 0.0);
        } catch (IOException e) {
            System.err.println("Erro ao carregar: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // =========================================================
    // MENUS — Cadastros
    // =========================================================
    @FXML public void handleMenuItemCadastrosClientes()        { loadView("/javafxmvc/view/FXMLCadastrosCliente.fxml"); }
    @FXML public void handleMenuItemCadastrosFabricaParceira() { loadView("/javafxmvc/view/FXMLCadastrosFabricaParceira.fxml"); }
    @FXML public void handleMenuItemCadastrosTipoServico()     { loadView("/javafxmvc/view/FXMLCadastrosTipoServico.fxml"); }

    // =========================================================
    // MENUS — Processos
    // =========================================================
    @FXML public void handleMenuItemProcessosAtendimento()   { loadView("/javafxmvc/view/FXMLProcessosAtendimento.fxml"); }
    @FXML public void handleMenuItemProcessosTerceirizacao() { loadView("/javafxmvc/view/FXMLProcessosTerceirizacao.fxml"); }
    @FXML public void handleMenuItemProcessosRetorno()       { loadView("/javafxmvc/view/FXMLProcessosRetorno.fxml"); }

    // =========================================================
    // MENUS — Gráficos
    // =========================================================
    @FXML public void handleMenuItemGraficosStatus()   { loadView("/javafxmvc/view/FXMLGraficosStatus.fxml"); }
    @FXML public void handleMenuItemGraficosVolume()   { loadView("/javafxmvc/view/FXMLGraficosVolume.fxml"); }
    @FXML public void handleMenuItemGraficosReceita()  { loadView("/javafxmvc/view/FXMLGraficosReceita.fxml"); }

    // =========================================================
    // MENUS — Relatórios (compila .jrxml em runtime)
    // =========================================================
    @FXML
    public void handleMenuItemRelatoriosOrdensAtraso() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Filtro do Relatório");
        dialog.setHeaderText("Relatório de Ordens em Atraso");
        dialog.setContentText("Digite o nome da Fábrica (ou deixe em branco para todas):");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            Map<String, Object> parametros = new HashMap<>();
            String filtro = result.get().trim();
            if (filtro.isEmpty()) {
                parametros.put("nome_fabrica", "%");
            } else {
                parametros.put("nome_fabrica", "%" + filtro + "%");
            }
            abrirRelatorio("/javafxmvc/relatorios/RelatorioOrdensAtraso.jrxml", "Ordens em Atraso por Fábrica", parametros);
        }
    }

    @FXML
    public void handleMenuItemRelatoriosRankingFabricas() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Filtro do Relatório");
        dialog.setHeaderText("Ranking de Fábricas");
        dialog.setContentText("Digite a especialidade (ou deixe em branco para todas):");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            Map<String, Object> parametros = new HashMap<>();
            String filtro = result.get().trim();
            if (filtro.isEmpty()) {
                parametros.put("especialidade", "%");
            } else {
                parametros.put("especialidade", "%" + filtro + "%");
            }
            abrirRelatorio("/javafxmvc/relatorios/RelatorioRankingFabricas.jrxml", "Ranking de Fábricas Parceiras", parametros);
        }
    }

    @FXML
    public void handleMenuItemRelatoriosReceitaServicos() {
        loadView("/javafxmvc/view/FXMLRelatorioReceitaServicos.fxml");
    }

    /**
     * Compila o arquivo .jrxml em memória (sem precisar de .jasper pré-compilado),
     * preenche com dados do banco e exibe no JasperViewer.
     */
    private void abrirRelatorio(String jrxmlPath, String titulo, Map<String, Object> parametros) {
        try {
            URL url = getClass().getResource(jrxmlPath);
            if (url == null) {
                mostrarErroRelatorio("Arquivo de relatório não encontrado:\n" + jrxmlPath);
                return;
            }
            Database db = DatabaseFactory.getDatabase("postgresql");
            Connection conn = db.conectar();
            JasperReport jasperReport = JasperCompileManager.compileReport(url.openStream());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conn);
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setTitle(titulo + " — Ateliê de Customização");
            jasperViewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErroRelatorio("Erro ao gerar o relatório \"" + titulo + "\":\n" + e.getMessage());
        }
    }

    private void mostrarErroRelatorio(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no Relatório");
        alert.setHeaderText("Não foi possível gerar o relatório.");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // =========================================================
    // MENU — Sistema (Sair / Logout)
    // =========================================================
    @FXML
    public void handleMenuItemSair() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLLogin.fxml"));
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/javafxmvc/view/atelie.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Ateliê de Customização - Login");
            stage.setResizable(false);
            stage.setWidth(800);
            stage.setHeight(500);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
