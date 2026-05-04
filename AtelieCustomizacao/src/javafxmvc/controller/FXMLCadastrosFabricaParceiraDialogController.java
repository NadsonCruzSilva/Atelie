package javafxmvc.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.domain.FabricaParceira;

public class FXMLCadastrosFabricaParceiraDialogController implements Initializable {

    @FXML private TextField textFieldNome;
    @FXML private TextField textFieldCnpj;
    @FXML private TextField textFieldTelefone;
    @FXML private TextField textFieldEspecialidade;
    @FXML private Button buttonConfirmar;
    @FXML private Button buttonCancelar;

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private FabricaParceira fabrica;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public Stage getDialogStage() { return dialogStage; }
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }
    public boolean isButtonConfirmarClicked() { return buttonConfirmarClicked; }

    public FabricaParceira getFabricaParceira() { return fabrica; }
    public void setFabricaParceira(FabricaParceira fabrica) {
        this.fabrica = fabrica;
        textFieldNome.setText(fabrica.getNome() != null ? fabrica.getNome() : "");
        textFieldCnpj.setText(fabrica.getCnpj() != null ? fabrica.getCnpj() : "");
        textFieldTelefone.setText(fabrica.getTelefone() != null ? fabrica.getTelefone() : "");
        textFieldEspecialidade.setText(fabrica.getEspecialidade() != null ? fabrica.getEspecialidade() : "");
    }

    @FXML
    public void handleButtonConfirmar() {
        if (validarEntradaDeDados()) {
            fabrica.setNome(textFieldNome.getText());
            fabrica.setCnpj(textFieldCnpj.getText());
            fabrica.setTelefone(textFieldTelefone.getText());
            fabrica.setEspecialidade(textFieldEspecialidade.getText());
            buttonConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleButtonCancelar() { dialogStage.close(); }

    private boolean validarEntradaDeDados() {
        String errorMessage = "";
        if (textFieldNome.getText() == null || textFieldNome.getText().isEmpty())
            errorMessage += "Nome inválido!\n";
        if (textFieldCnpj.getText() == null || textFieldCnpj.getText().isEmpty())
            errorMessage += "CNPJ inválido!\n";

        if (errorMessage.isEmpty()) return true;
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no Cadastro");
        alert.setHeaderText("Campos inválidos, por favor corrija...");
        alert.setContentText(errorMessage);
        alert.show();
        return false;
    }
}
