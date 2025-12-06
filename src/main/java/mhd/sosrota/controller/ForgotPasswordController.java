package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.exceptions.AuthenticationException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.presentation.PasswordToggle;
import mhd.sosrota.service.UsuarioService;
import mhd.sosrota.util.AlertUtil;

public class ForgotPasswordController implements Navigable {
    @FXML
    private PasswordField novaSenhaField, confirmarSenhaField;

    @FXML
    private TextField usernameField, novaSenhaVisibleField, confirmarSenhaVisibleField;

    @FXML
    private ImageView novaButtonImageView, confirmarButtonImageView;

    private PasswordToggle novaToggle, confirmarToggle;

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

        novaToggle = new PasswordToggle(novaSenhaField, novaSenhaVisibleField, novaButtonImageView);
        confirmarToggle = new PasswordToggle(confirmarSenhaField, confirmarSenhaVisibleField, confirmarButtonImageView);

        novaToggle.setShowing(false);
        confirmarToggle.setShowing(false);
    }

    @FXML
    private void handleSalvar() {
        limparErro();

        String username = usernameField.getText();
        String novaSenha = novaSenhaField.getText();
        String confirmarSenha = confirmarSenhaField.getText();

        if (!novaSenha.equals(confirmarSenha)) {
            mostrarErro("As senhas digitadas não são iguais.");
            return;
        }

        try {
            service.redefinirSenha(username, novaSenha);
            AlertUtil.showInfo("Sucesso", "Senha redefinida com sucesso!");
            handleVoltar();
        } catch (AuthenticationException e) {
            mostrarErro(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao tentar redefinir senha.");
        }
    }

    @FXML
    private void handleVoltar() {
        navigator.closeStage(salvarButton);
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

    @FXML
    private void toggleNovaSenha() {
        novaToggle.toggle();
    }

    @FXML
    private void toggleConfirmarSenha() {
        confirmarToggle.toggle();
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}

