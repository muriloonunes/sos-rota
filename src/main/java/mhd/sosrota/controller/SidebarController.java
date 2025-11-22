package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mhd.sosrota.util.FXMLLoaderHelper;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static mhd.sosrota.SOSRota.*;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class DashboardController
 */
public class SidebarController {
    @FXML
    private StackPane contentArea;
    @FXML
    private HBox dashboardHbox, ocorrenciasHbox, ambulanciasHbox, equipesHbox, relatoriosHbox, logoutHbox;
    @FXML
    private Button dashboardButton, ocorrenciasButton, ambulanciasButton, equipesButton, relatoriosButton;
    @FXML
    private Label bemVindoLabel;

    private List<HBox> navButtons;

    @FXML
    public void initialize() {
        SVGImage dashboardIcon = SVGLoader.load(getClass().getResource("/images/home.svg")).scaleTo(24);
        SVGImage ocorrenciasIcon = SVGLoader.load(getClass().getResource("/images/ocorrencias.svg")).scaleTo(24);
        SVGImage ambulanciasIcon = SVGLoader.load(getClass().getResource("/images/ambulancias.svg")).scaleTo(24);
        SVGImage equipesIcon = SVGLoader.load(getClass().getResource("/images/equipes.svg")).scaleTo(24);
        SVGImage relatoriosIcon = SVGLoader.load(getClass().getResource("/images/relatorios.svg")).scaleTo(24);
        SVGImage logoutIcon = SVGLoader.load(getClass().getResource("/images/logout.svg")).scaleTo(18);
        dashboardHbox.getChildren().addFirst(dashboardIcon);
        ocorrenciasHbox.getChildren().addFirst(ocorrenciasIcon);
        ambulanciasHbox.getChildren().addFirst(ambulanciasIcon);
        equipesHbox.getChildren().addFirst(equipesIcon);
        relatoriosHbox.getChildren().addFirst(relatoriosIcon);
        logoutHbox.getChildren().addFirst(logoutIcon);

        navButtons = new ArrayList<>();
        navButtons.add(dashboardHbox);
        navButtons.add(ocorrenciasHbox);
        navButtons.add(ambulanciasHbox);
        navButtons.add(equipesHbox);
        navButtons.add(relatoriosHbox);

        handleDashboardClick(); //como o dashboard é a tela que o programa abre, ja carregamos no initialize
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
            contentArea.getChildren().setAll(view);
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
    private void setActiveButton(HBox activeButton) {
        for (HBox button : navButtons) {
            button.getStyleClass().remove("hbox-button-active"); // Remove o estilo ativo de todos
            if (!button.getStyleClass().contains("hbox-button")) {
                button.getStyleClass().add("hbox-button"); // Garante o estilo padrão
            }
        }
        activeButton.getStyleClass().remove("hbox-button"); // Remove o estilo padrão (se presente)
        activeButton.getStyleClass().add("hbox-button-active"); // Adiciona o estilo ativo ao botão clicado
    }

    @FXML
    public void handleDashboardClick() {
        setActiveButton(dashboardHbox);
        setContent(DASHBOARD_OVERVIEW);
    }

    @FXML
    public void handleOcorrenciasClick() {
        setActiveButton(ocorrenciasHbox);
        setContent(DASHBOARD_OCCORRENCIAS);
    }

    @FXML
    public void handleAmbulanciasClick() {
        setActiveButton(ambulanciasHbox);
        setContent(DASHBOARD_AMBULANCIAS);
    }

    @FXML
    public void handleProfissionaisClick() {
        setActiveButton(equipesHbox);
        setContent(DASHBOARD_EQUIPES);
    }

    @FXML
    public void handleRelatoriosClick() {
        setActiveButton(relatoriosHbox);
        setContent(DASHBOARD_RELATORIOS);
    }

    @FXML
    public void handleLogout() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(TELA_LOGIN));
            Parent root = loader.load();
            Scene scene = FXMLLoaderHelper.createScene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}