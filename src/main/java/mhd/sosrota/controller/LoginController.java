package mhd.sosrota.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
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
import mhd.sosrota.util.AlertUtil;

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
    private Button loginButton, cadastrarButton, voltarLoginButton, abrirCadastrarButton;

    private PasswordToggle loginToggle, cadastrarToggle;

    private Navigator navigator;

    private final UsuarioService service =
            new UsuarioService(
                    new UsuarioRepositoryImpl()
            );

    private final BooleanProperty loading = new SimpleBooleanProperty(false);


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

        setLoading(true, loginButton);

        Task<Usuario> task = new Task<>() {
            @Override
            protected Usuario call() throws Exception {
                return service.autenticar(username, senha);
            }

            @Override
            protected void succeeded() {
                setLoading(false, loginButton);
                Usuario usuario = getValue();
                service.salvarUsuario(usuario.getNome(), usuario.getUsername());

                loadDashboardScreen();
            }

            @Override
            protected void failed() {
                setLoading(false, loginButton);

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

        setLoading(true, cadastrarButton);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return service.cadastrarUsuario(nome, username, senha);
            }

            @Override
            protected void succeeded() {
                setLoading(false, cadastrarButton);
                boolean cadastro = getValue();
                if (cadastro) {
                    AlertUtil.showInfo("Cadastro realizado", "Usuário cadastrado com sucesso!");
                    handleVoltarParaLogin();
                }
            }

            @Override
            protected void failed() {
                setLoading(false, cadastrarButton);

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

    private void loadDashboardScreen() {
        navigator.navigate(Screens.TELA_APP);
    }

    private void setLoading(boolean isLoading, Button button) {
        loading.set(isLoading);
        if (isLoading) {
            button.setUserData(button.getText());
            ProgressIndicator pi = new ProgressIndicator();

            pi.setPrefSize(16, 16);

            button.setText(null);
            button.setGraphic(pi);
            button.setMouseTransparent(true);
            button.setFocusTraversable(true);
        } else {
            String originalText = (String) button.getUserData();

            button.setText(originalText);
            button.setGraphic(null);

            button.setUserData(null);
            button.setMouseTransparent(false);
            button.setFocusTraversable(false);
        }
    }

    @FXML
    private void toggleLoginPassword() {
        loginToggle.toggle();
    }

    @FXML
    private void toggleCadastrarPassword() {
        cadastrarToggle.toggle();
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
