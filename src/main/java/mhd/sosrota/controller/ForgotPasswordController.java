package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.exceptions.AuthenticationException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.service.UsuarioService;
import mhd.sosrota.util.AlertUtil;

public class ForgotPasswordController implements Navigable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField novaSenhaField;

    @FXML
    private PasswordField confirmarSenhaField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button salvarButton;

    private Navigator navigator;

    private final UsuarioService service = AppContext.getInstance().getUsuarioService();

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        salvarButton.disableProperty().bind(
                usernameField.textProperty().isEmpty()
                        .or(novaSenhaField.textProperty().isEmpty())
                        .or(confirmarSenhaField.textProperty().isEmpty())
        );
    }

    @FXML
    private void handleSalvar() {
        limparErro();

        String username = usernameField.getText();
        String novaSenha = novaSenhaField.getText();
        String confirmarSenha = confirmarSenhaField.getText();

        if (!novaSenha.equals(confirmarSenha)) {
            mostrarErro("A nova senha e a confirmação não são iguais.");
            return;
        }

        try {
            service.redefinirSenha(username, novaSenha);
            AlertUtil.showInfo("Sucesso", "Senha redefinida com sucesso!");
            // Volta pra tela de login depois de salvar
            navigator.navigate(Screens.TELA_LOGIN);
        } catch (AuthenticationException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao tentar redefinir senha.");
        }
    }

    @FXML
    private void handleVoltar() {
        navigator.navigate(Screens.TELA_LOGIN);
    }

    private void mostrarErro(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void limparErro() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}

