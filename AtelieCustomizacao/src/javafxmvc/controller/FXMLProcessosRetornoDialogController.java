package javafxmvc.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class FXMLProcessosRetornoDialogController implements Initializable {

    @FXML private Button buttonConfirmar;
    @FXML private Button buttonCancelar;

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public Stage getDialogStage() { return dialogStage; }
    public void setDialogStage(Stage dialogStage) { this.dialogStage = dialogStage; }
    public boolean isButtonConfirmarClicked() { return buttonConfirmarClicked; }

    @FXML
    public void handleButtonConfirmar() {
        buttonConfirmarClicked = true;
        dialogStage.close();
    }

    @FXML
    public void handleButtonCancelar() {
        dialogStage.close();
    }
}
