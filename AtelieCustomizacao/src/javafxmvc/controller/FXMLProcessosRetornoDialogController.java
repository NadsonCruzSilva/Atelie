package javafxmvc.controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafxmvc.model.domain.OrdemServico;

public class FXMLProcessosRetornoDialogController implements Initializable {

    @FXML private Label     labelOsId;
    @FXML private Label     labelCliente;
    @FXML private Label     labelAbertura;
    @FXML private Label     labelPrevista;
    @FXML private Label     labelStatus;
    @FXML private Label     labelValorRegistrado;
    @FXML private TextField textFieldValorFinal;
    @FXML private Button    buttonConfirmar;
    @FXML private Button    buttonCancelar;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Stage       dialogStage;
    private OrdemServico ordemServico;
    private boolean     buttonConfirmarClicked = false;
    /** Valor final informado pelo usuário (ou -1 se não informado) */
    private double      valorFinalInformado = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    // ========================================================
    // Getters / Setters
    // ========================================================
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isButtonConfirmarClicked() {
        return buttonConfirmarClicked;
    }

    /** Retorna o valor informado pelo usuário, ou -1 se campo estava vazio. */
    public double getValorFinalInformado() {
        return valorFinalInformado;
    }

    /**
     * Popula os labels com os dados da OS selecionada.
     * Deve ser chamado pelo controller pai antes de showAndWait().
     */
    public void setOrdemServico(OrdemServico os) {
        this.ordemServico = os;

        labelOsId.setText(String.valueOf(os.getId()));
        labelCliente.setText(os.getCliente() != null ? os.getCliente().getNome() : "—");
        labelAbertura.setText(os.getDataAbertura() != null ? os.getDataAbertura().format(FMT) : "—");
        labelPrevista.setText(os.getDataPrevista()  != null ? os.getDataPrevista().format(FMT)  : "—");
        labelStatus.setText(os.getStatus());
        labelValorRegistrado.setText(String.format("R$ %.2f", os.getValorTotal()));
    }

    // ========================================================
    // Handlers de botão
    // ========================================================
    @FXML
    public void handleButtonConfirmar() {
        String textoValor = textFieldValorFinal.getText();
        if (textoValor != null && !textoValor.trim().isEmpty()) {
            try {
                valorFinalInformado = Double.parseDouble(textoValor.replace(",", "."));
                if (valorFinalInformado < 0) {
                    mostrarErro("O valor final não pode ser negativo.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarErro("Valor final inválido. Use números (ex: 120.50).");
                return;
            }
        } else {
            valorFinalInformado = -1; // indica que o usuário não alterou o valor
        }
        buttonConfirmarClicked = true;
        dialogStage.close();
    }

    @FXML
    public void handleButtonCancelar() {
        dialogStage.close();
    }

    // ========================================================
    // Utilitário
    // ========================================================
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Campo inválido");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
