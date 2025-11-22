package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mhd.sosrota.util.FXMLLoaderHelper;

import java.io.IOException;
import java.util.Objects;

import static mhd.sosrota.SOSRota.TELA_APP;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class LoginController
 */
public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private TextField passwordVisibleField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView buttonImageView;

    private boolean isPasswordVisible = false;

    public void initialize() {
        passwordField.setVisible(true);
        passwordField.setManaged(true);
        passwordVisibleField.setVisible(false);
        passwordVisibleField.setManaged(false);
        errorMessageLabel.setVisible(false);
        errorMessageLabel.setManaged(false);
        passwordField.textProperty().addListener((_, _, text) -> {
            if (!isPasswordVisible) passwordVisibleField.setText(text);
        });
        passwordVisibleField.textProperty().addListener((_, _, text) -> {
            if (isPasswordVisible) passwordField.setText(text);
        });
    }

    @FXML
    private void handleLoginButtonAction() {
        //TODO
        loadDashboardScreen();
    }

    @FXML
    private void handleForgotPasswordAction() {

    }

    @FXML
    private void handleCadastrarButtonAction() {

    }

    @FXML
    private void togglePassword() {
        if (isPasswordVisible) {
            passwordField.setText(passwordVisibleField.getText());
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);

            passwordField.setVisible(true);
            passwordField.setManaged(true);

            buttonImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/visibility_off.png"))));
            isPasswordVisible = false;
        } else {
            passwordVisibleField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);

            passwordVisibleField.setVisible(true);
            passwordVisibleField.setManaged(true);

            buttonImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/visibility_on.png"))));
            isPasswordVisible = true;
        }
    }

    private void loadDashboardScreen() {
        //TODO
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            FXMLLoader dashboardLoader = FXMLLoaderHelper.loadFXML(TELA_APP);
            Parent root = dashboardLoader.getRoot();

            Scene scene = FXMLLoaderHelper.createScene(root);

            stage.setScene(scene);
            stage.show();
            // stage.setFullScreen(true);
            // stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Erro ao carregar a tela do Dashboard.");
            errorMessageLabel.setVisible(true);
            errorMessageLabel.setManaged(true);
        }
    }
}
