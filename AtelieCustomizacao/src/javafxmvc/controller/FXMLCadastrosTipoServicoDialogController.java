package javafxmvc.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.domain.TipoServico;

public class FXMLCadastrosTipoServicoDialogController implements Initializable {

    @FXML private TextField textFieldDescricao;
    @FXML private TextField textFieldValor;
    @FXML private TextField textFieldPrazo;
    @FXML private Button buttonConfirmar;
    @FXML private Button buttonCancelar;

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private TipoServico tipoServico;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public Stage getDialogStage() { return dialogStage; }
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }
    public boolean isButtonConfirmarClicked() { return buttonConfirmarClicked; }

    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
        textFieldDescricao.setText(tipoServico.getDescricao() != null ? tipoServico.getDescricao() : "");
        textFieldValor.setText(tipoServico.getValor() > 0 ? String.valueOf(tipoServico.getValor()) : "");
        textFieldPrazo.setText(tipoServico.getPrazoEstimadoDias() > 0 ? String.valueOf(tipoServico.getPrazoEstimadoDias()) : "");
    }

    @FXML
    public void handleButtonConfirmar() {
        if (validarEntradaDeDados()) {
            tipoServico.setDescricao(textFieldDescricao.getText());
            try {
                tipoServico.setValor(Double.parseDouble(textFieldValor.getText().replace(",", ".")));
                tipoServico.setPrazoEstimadoDias(Integer.parseInt(textFieldPrazo.getText()));
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no Cadastro");
                alert.setHeaderText("Campos numéricos inválidos!");
                alert.setContentText("Valor e Prazo devem ser números.");
                alert.show();
                return;
            }
            buttonConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleButtonCancelar() { dialogStage.close(); }

    private boolean validarEntradaDeDados() {
        String errorMessage = "";
        if (textFieldDescricao.getText() == null || textFieldDescricao.getText().isEmpty())
            errorMessage += "Descrição inválida!\n";
        if (textFieldValor.getText() == null || textFieldValor.getText().isEmpty())
            errorMessage += "Valor inválido!\n";
        if (textFieldPrazo.getText() == null || textFieldPrazo.getText().isEmpty())
            errorMessage += "Prazo inválido!\n";

        if (errorMessage.isEmpty()) return true;
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro no Cadastro");
        alert.setHeaderText("Campos inválidos, por favor corrija...");
        alert.setContentText(errorMessage);
        alert.show();
        return false;
    }
}
