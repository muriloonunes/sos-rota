package mhd.sosrota.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Usuario;
import mhd.sosrota.model.exceptions.AuthenticationException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.presentation.PasswordToggle;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.UsuarioService;
import mhd.sosrota.util.AlertUtil;

import java.sql.SQLException;

/**
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class LoginController
 */
public class LoginController implements Navigable {
    @FXML
    private Label loginErrorMessageLabel, cadastrarErrorMessageLabel;

    @FXML
    private TextField cadastrarNomeField, cadastrarUsernameField, loginUsernameField, loginPasswordVisibleField, cadastrarPasswordVisibleField, cadastrarConfirmarPasswordVisibleField;

    @FXML
    private PasswordField cadastrarPasswordField, loginPasswordField, cadastrarConfirmarSenhaField;

    @FXML
    private ImageView loginButtonImageView, cadastrarButtonImageView, cadastrarConfirmarButtonImageView;

    @FXML
    private VBox cadastrarForm, loginForm;

    @FXML
    private Button loginButton, cadastrarButton, voltarLoginButton, abrirCadastrarButton;

    private PasswordToggle loginToggle, cadastrarToggle, cadastrarConfirmarToggle;

    private Navigator navigator;

    private final UsuarioService service = AppContext.getInstance().getUsuarioService();

    private final BooleanProperty loading = new SimpleBooleanProperty(false);


    public void initialize() {
        loginToggle = new PasswordToggle(loginPasswordField, loginPasswordVisibleField, loginButtonImageView);
        cadastrarToggle = new PasswordToggle(cadastrarPasswordField, cadastrarPasswordVisibleField, cadastrarButtonImageView);
        cadastrarConfirmarToggle = new PasswordToggle(cadastrarConfirmarSenhaField, cadastrarConfirmarPasswordVisibleField, cadastrarConfirmarButtonImageView);

        loginButton.disableProperty().bind(
                loginUsernameField.textProperty().isEmpty()
                        .or(loginPasswordField.textProperty().isEmpty())
        );

        cadastrarButton.disableProperty().bind(
                cadastrarNomeField.textProperty().isEmpty()
                        .or(cadastrarUsernameField.textProperty().isEmpty()
                                .or(cadastrarPasswordField.textProperty().isEmpty()
                                        .or(cadastrarConfirmarSenhaField.textProperty().isEmpty())))
        );

        voltarLoginButton.disableProperty().bind(loading);

        abrirCadastrarButton.disableProperty().bind(loading);

        handleVoltarParaLogin();
    }

    @FXML
    private void handleLoginButtonAction() {
        loginErrorMessageLabel.setVisible(false);
        loginErrorMessageLabel.setManaged(false);
        String username = loginUsernameField.getText();
        String senha = loginPasswordField.getText();

        UiUtils.setButtonLoading(loginButton, true, "Entrar");

        Task<Usuario> task = new Task<>() {
            @Override
            protected Usuario call() throws Exception {
                return service.autenticar(username, senha);
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(loginButton, false, "Entrar");
                Usuario usuario = getValue();
                service.salvarUsuario(usuario.getNome(), usuario.getUsername());

                loadDashboardScreen();
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(loginButton, false, "Entrar");

                Throwable e = getException();
                if (e instanceof AuthenticationException) {
                    mostrarErroLogin(e.getMessage());
                } else {
                    e.printStackTrace();
                    mostrarErroLogin("Erro ao conectar ao banco de dados");
                }
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleCadastrar() {
        cadastrarErrorMessageLabel.setVisible(false);
        cadastrarErrorMessageLabel.setManaged(false);

        String nome = cadastrarNomeField.getText();
        String username = cadastrarUsernameField.getText();
        String senha = cadastrarPasswordField.getText();
        String confirmar = cadastrarConfirmarSenhaField.getText();

        if (!senha.equals(confirmar)) {
            mostrarErroCadastro("As senhas digitadas não são iguais.");
            return;
        }

        UiUtils.setButtonLoading(cadastrarButton, true, "Cadastrar");

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return service.cadastrarUsuario(nome, username, senha);
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(cadastrarButton, false, "Cadastrar");
                boolean cadastro = getValue();
                if (cadastro) {
                    AlertUtil.showInfo("Cadastro realizado", "Usuário cadastrado com sucesso!");
                    handleVoltarParaLogin();
                }
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(cadastrarButton, false, "Cadastrar");

                Throwable e = getException();
                if (e instanceof AuthenticationException) {
                    mostrarErroCadastro(e.getMessage());
                } else if (e instanceof SQLException) {
                    e.printStackTrace();
                    mostrarErroCadastro("Erro no sistema.");
                } else {
                    e.printStackTrace();
                    mostrarErroCadastro("Algo deu errado.");
                }
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleForgotPasswordAction() {
        //TODO
        //Fiz o ForgotPasswordController, forgot_password_screen.fxml, e adicionei na enum Screens
        navigator.showModal(Screens.TELA_REDEFINIR_SENHA, "Redefinir Senha", 400.0, 450.0);
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
        cadastrarConfirmarToggle.setShowing(false);

        cadastrarNomeField.clear();
        cadastrarUsernameField.clear();
        cadastrarPasswordField.clear();
        cadastrarConfirmarSenhaField.clear();
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

    private void loadDashboardScreen() {
        navigator.navigate(Screens.TELA_APP);
    }

    @FXML
    private void toggleLoginPassword() {
        loginToggle.toggle();
    }

    @FXML
    private void toggleCadastrarPassword() {
        cadastrarToggle.toggle();
    }

    @FXML
    private void toggleCadastrarConfirmarPassword() {
        cadastrarConfirmarToggle.toggle();
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
