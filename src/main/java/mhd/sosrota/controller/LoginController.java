package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import mhd.sosrota.model.Usuario;
import mhd.sosrota.model.exceptions.AuthenticationException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.presentation.PasswordToggle;
import mhd.sosrota.repository.UsuarioRepositoryImpl;
import mhd.sosrota.service.UsuarioService;

import java.sql.SQLException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class LoginController
 */
public class LoginController implements Navigable {
    @FXML
    private Label loginErrorMessageLabel, cadastrarErrorMessageLabel;

    @FXML
    private TextField cadastrarNomeField, cadastrarUsernameField, loginUsernameField, loginPasswordVisibleField, cadastrarPasswordVisibleField;

    @FXML
    private PasswordField cadastrarPasswordField, loginPasswordField;

    @FXML
    private ImageView loginButtonImageView, cadastrarButtonImageView;

    @FXML
    private VBox cadastrarForm, loginForm;

    @FXML
    private Button loginButton, cadastrarButton;

    private PasswordToggle loginToggle, cadastrarToggle;

    private Navigator navigator;

    private final UsuarioService service =
            new UsuarioService(
                    new UsuarioRepositoryImpl()
            );

    public void initialize() {
        loginToggle = new PasswordToggle(loginPasswordField, loginPasswordVisibleField, loginButtonImageView);
        cadastrarToggle = new PasswordToggle(cadastrarPasswordField, cadastrarPasswordVisibleField, cadastrarButtonImageView);

        loginButton.disableProperty().bind(
                loginUsernameField.textProperty().isEmpty()
                        .or(loginPasswordField.textProperty().isEmpty())
        );

        cadastrarButton.disableProperty().bind(
                cadastrarNomeField.textProperty().isEmpty()
                        .or(cadastrarUsernameField.textProperty().isEmpty()
                                .or(cadastrarPasswordField.textProperty().isEmpty()))
        );

        handleVoltarParaLogin();
    }

    @FXML
    private void handleLoginButtonAction() {
        loginErrorMessageLabel.setVisible(false);
        loginErrorMessageLabel.setManaged(false);
        String username = loginUsernameField.getText();
        String senha = loginPasswordField.getText();
        try {
            Usuario usuario = service.autenticar(username, senha);
            System.out.println(usuario);
        } catch (AuthenticationException e) {
            mostrarErroLogin(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErroLogin("Erro ao conectar ao banco de dados");
        }
        //        loadDashboardScreen();
    }

    @FXML
    private void handleCadastrar() {
        cadastrarErrorMessageLabel.setVisible(false);
        cadastrarErrorMessageLabel.setManaged(false);

        String nome = cadastrarNomeField.getText();
        String username = cadastrarUsernameField.getText();
        String senha = cadastrarPasswordField.getText();
        try {
            boolean cadastro = service.cadastrarUsuario(nome, username, senha);
            if (cadastro) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Cadastro realizado");
                alert.setHeaderText(null);
                alert.setContentText("Usuário cadastrado com sucesso!");
                alert.showAndWait();
                handleVoltarParaLogin();
            }
        } catch (AuthenticationException e) {
            mostrarErroCadastro(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarErroCadastro("Erro no sistema.");
        }
    }

    @FXML
    private void handleForgotPasswordAction() {

    }

    @FXML
    private void handleMostrarCadastro() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        cadastrarForm.setVisible(true);
        cadastrarForm.setManaged(true);
        cadastrarErrorMessageLabel.setManaged(false);
        cadastrarErrorMessageLabel.setVisible(false);

        cadastrarToggle.setShowing(false);

        cadastrarNomeField.clear();
        cadastrarUsernameField.clear();
        cadastrarPasswordField.clear();
    }

    @FXML
    private void handleVoltarParaLogin() {
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        cadastrarForm.setVisible(false);
        cadastrarForm.setManaged(false);
        loginErrorMessageLabel.setManaged(false);
        loginErrorMessageLabel.setVisible(false);

        loginToggle.setShowing(false);

        loginUsernameField.clear();
        loginPasswordField.clear();

    }

    @FXML
    private void toggleLoginPassword() {
        loginToggle.toggle();
    }

    @FXML
    private void toggleCadastrarPassword() {
        cadastrarToggle.toggle();
    }

    private void loadDashboardScreen() {
        //TODO
        navigator.navigate(Screens.TELA_APP);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    private void mostrarErroLogin(String erro) {
        loginErrorMessageLabel.setText(erro);
        loginErrorMessageLabel.setVisible(true);
        loginErrorMessageLabel.setManaged(true);
    }

    private void mostrarErroCadastro(String erro) {
        cadastrarErrorMessageLabel.setText(erro);
        cadastrarErrorMessageLabel.setVisible(true);
        cadastrarErrorMessageLabel.setManaged(true);
    }
}
