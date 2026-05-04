package javafxmvc.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.domain.Cliente;

public class FXMLCadastrosClienteDialogController implements Initializable {

    @FXML private TextField textFieldNome;
    @FXML private TextField textFieldCpf;
    @FXML private TextField textFieldTelefone;
    @FXML private TextField textFieldEndereco;
    @FXML private Button buttonConfirmar;
    @FXML private Button buttonCancelar;

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Cliente cliente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public Stage getDialogStage() { return dialogStage; }
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }
    public boolean isButtonConfirmarClicked() { return buttonConfirmarClicked; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        textFieldNome.setText(cliente.getNome() != null ? cliente.getNome() : "");
        textFieldCpf.setText(cliente.getCpf() != null ? cliente.getCpf() : "");
        textFieldTelefone.setText(cliente.getTelefone() != null ? cliente.getTelefone() : "");
        textFieldEndereco.setText(cliente.getEndereco() != null ? cliente.getEndereco() : "");
    }

    @FXML
    public void handleButtonConfirmar() {
        if (validarEntradaDeDados()) {
            cliente.setNome(textFieldNome.getText());
            cliente.setCpf(textFieldCpf.getText());
            cliente.setTelefone(textFieldTelefone.getText());
            cliente.setEndereco(textFieldEndereco.getText());
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
        if (textFieldCpf.getText() == null || textFieldCpf.getText().isEmpty())
            errorMessage += "CPF inválido!\n";

        if (errorMessage.isEmpty()) return true;
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no Cadastro");
        alert.setHeaderText("Campos inválidos, por favor corrija...");
        alert.setContentText(errorMessage);
        alert.show();
        return false;
    }
}
