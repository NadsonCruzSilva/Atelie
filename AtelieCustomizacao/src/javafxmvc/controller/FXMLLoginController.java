package javafxmvc.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLLoginController implements Initializable {

    @FXML private TextField textFieldUsuario;
    @FXML private PasswordField passwordFieldSenha;
    @FXML private Label labelErro;
    @FXML private Button buttonEntrar;

    // Credenciais fixas (em produção, consultar banco de dados)
    private static final String USUARIO_PADRAO = "admin";
    private static final String SENHA_PADRAO = "admin";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        labelErro.setText("");
    }

    @FXML
    public void handleButtonEntrar() {
        String usuario = textFieldUsuario.getText();
        String senha = passwordFieldSenha.getText();

        if (usuario == null || usuario.isEmpty()) {
            labelErro.setText("Por favor, informe o usuário.");
            textFieldUsuario.requestFocus();
            return;
        }

        if (senha == null || senha.isEmpty()) {
            labelErro.setText("Por favor, informe a senha.");
            passwordFieldSenha.requestFocus();
            return;
        }

        if (USUARIO_PADRAO.equals(usuario) && SENHA_PADRAO.equals(senha)) {
            abrirTelaPrincipal();
        } else {
            labelErro.setText("Usuário ou senha incorretos!");
            passwordFieldSenha.clear();
            passwordFieldSenha.requestFocus();
            // Efeito visual de "shake" na label de erro
            labelErro.setStyle("-fx-text-fill: #e94560; -fx-font-weight: bold;");
        }
    }

    private void abrirTelaPrincipal() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/javafxmvc/view/FXMLVBoxMain.fxml"));
            Stage stage = (Stage) buttonEntrar.getScene().getWindow();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/javafxmvc/view/atelie.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Ateliê de Customização - Sistema de Gestão");
            stage.setResizable(true);
            stage.setWidth(900);
            stage.setHeight(650);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            labelErro.setText("Erro ao carregar a tela principal.");
        }
    }
}
