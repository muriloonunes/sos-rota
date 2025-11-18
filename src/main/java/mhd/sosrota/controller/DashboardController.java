package mhd.sosrota.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mhd.sosrota.util.FXMLLoaderHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static mhd.sosrota.SOSRota.DASHBOARD_OVERVIEW;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class DashboardController
 */
public class DashboardController {
    @FXML
    private StackPane contentArea;

    @FXML
    private Button dashboardNavButton;
    @FXML
    private Button occurrencesNavButton;
    @FXML
    private Button ambulancesNavButton;
    @FXML
    private Button professionalsNavButton;
    @FXML
    private Button reportsNavButton;

    private List<Button> navButtons;

    @FXML
    public void initialize() {
        navButtons = new ArrayList<>();
        navButtons.add(dashboardNavButton);
        navButtons.add(occurrencesNavButton);
        navButtons.add(ambulancesNavButton);
        navButtons.add(professionalsNavButton);
        navButtons.add(reportsNavButton);

        // Carrega o conteúdo inicial do Dashboard ao iniciar a tela
        handleDashboardClick(null);
    }

    /**
     * Carrega um FXML para a área de conteúdo principal do Dashboard.
     *
     * @param fxmlPath O caminho relativo do arquivo FXML a ser carregado.
     */
    private void setContent(String fxmlPath) {
        try {
            FXMLLoader loader = FXMLLoaderHelper.loadFXML(fxmlPath);
            Node view = loader.getRoot();
            contentArea.getChildren().setAll(view); // Substitui todo o conteúdo existente pelo novo
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela: " + fxmlPath);
            // Implementar lógica para exibir uma mensagem de erro na UI
        }
    }

    /**
     * Atualiza a classe de estilo do botão ativo na barra lateral.
     *
     * @param activeButton O botão que deve ser marcado como ativo.
     */
    private void setActiveButton(Button activeButton) {
        for (Button button : navButtons) {
            button.getStyleClass().remove("nav-button-active"); // Remove o estilo ativo de todos
            if (!button.getStyleClass().contains("nav-button")) {
                button.getStyleClass().add("nav-button"); // Garante o estilo padrão
            }
        }
        activeButton.getStyleClass().remove("nav-button"); // Remove o estilo padrão (se presente)
        activeButton.getStyleClass().add("nav-button-active"); // Adiciona o estilo ativo ao botão clicado
    }

    @FXML
    public void handleDashboardClick(ActionEvent event) {
        setActiveButton(dashboardNavButton);
        setContent(DASHBOARD_OVERVIEW); // Carrega o novo FXML de visão geral
    }

    @FXML
    public void handleOcorrenciasClick(ActionEvent event) {
        setActiveButton(occurrencesNavButton);
        // Usaremos a tela de Cadastro de Ocorrências para gerenciar (listar/cadastrar)
        setContent("/views/OccurrenceRegistrationScreen.fxml");
    }

    @FXML
    public void handleAmbulanciasClick(ActionEvent event) {
        setActiveButton(ambulancesNavButton);
        // Usaremos a tela de Cadastro de Ambulâncias para gerenciar
        setContent("/views/AmbulanceRegistrationScreen.fxml");
    }

    @FXML
    public void handleProfissionaisClick(ActionEvent event) {
        setActiveButton(professionalsNavButton);
        setContent("/views/TeamManagementScreen.fxml");
    }

    @FXML
    public void handleRelatoriosClick(ActionEvent event) {
        setActiveButton(reportsNavButton);
        setContent("/views/ReportsScreen.fxml");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        System.out.println("Usuário desconectado.");
        try {
            // Retorna para a tela de Login
            Stage stage = (Stage) contentArea.getScene().getWindow(); // Obtém o Stage a partir da área de conteúdo
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginScreen.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/login-screen.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("SOS-Rota - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}