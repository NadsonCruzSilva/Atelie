package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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

    @FXML public void handleMenuItemCadastrosClientes() { loadView("/javafxmvc/view/FXMLCadastrosCliente.fxml"); }
    @FXML public void handleMenuItemCadastrosFabricaParceira() { loadView("/javafxmvc/view/FXMLCadastrosFabricaParceira.fxml"); }
    @FXML public void handleMenuItemCadastrosTipoServico() { loadView("/javafxmvc/view/FXMLCadastrosTipoServico.fxml"); }
    @FXML public void handleMenuItemProcessosAtendimento() { loadView("/javafxmvc/view/FXMLProcessosAtendimento.fxml"); }
    @FXML public void handleMenuItemProcessosTerceirizacao() { loadView("/javafxmvc/view/FXMLProcessosTerceirizacao.fxml"); }
    @FXML public void handleMenuItemProcessosRetorno() { loadView("/javafxmvc/view/FXMLProcessosRetorno.fxml"); }
    @FXML public void handleMenuItemGraficosStatus() { loadView("/javafxmvc/view/FXMLGraficosStatus.fxml"); }
    @FXML public void handleMenuItemGraficosVolume() { loadView("/javafxmvc/view/FXMLGraficosVolume.fxml"); }
    @FXML public void handleMenuItemGraficosReceita() { loadView("/javafxmvc/view/FXMLGraficosReceita.fxml"); }

    @FXML public void handleMenuItemRelatoriosOrdensAtraso() { /* relatório JasperSoft */ }
    @FXML public void handleMenuItemRelatoriosRankingFabricas() { /* relatório JasperSoft */ }
    @FXML public void handleMenuItemRelatoriosReceitaServicos() { /* relatório JasperSoft */ }

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
