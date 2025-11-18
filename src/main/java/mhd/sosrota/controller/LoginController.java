package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mhd.sosrota.util.FXMLLoaderHelper;

import java.io.IOException;

import static mhd.sosrota.SOSRota.TELA_DASHBOARD;

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
    private void handleLoginButtonAction() {
        //TODO
        loadDashboardScreen();
    }

    @FXML
    private void handleRegisterAction() {

    }

    @FXML
    private void handleForgotPasswordAction() {

    }

    private void loadDashboardScreen() {
        //TODO
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();

            FXMLLoader dashboardLoader = FXMLLoaderHelper.loadFXML(TELA_DASHBOARD);
            Parent root = dashboardLoader.getRoot();

            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("SOS-Rota - Dashboard");
            stage.show();
            // stage.setFullScreen(true); // Opcional: para tela cheia
            // stage.setMaximized(true);  // Opcional: para maximizar
        } catch (IOException e) {
            e.printStackTrace();
            errorMessageLabel.setText("Erro ao carregar a tela do Dashboard.");
            errorMessageLabel.setVisible(true);
            errorMessageLabel.setManaged(true);
        }
    }
}
