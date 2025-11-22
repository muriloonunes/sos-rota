package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class DashboardController
 */
public class SidebarController implements Navigable {
    @FXML
    private StackPane contentArea;
    @FXML
    private HBox dashboardHbox, ocorrenciasHbox, ambulanciasHbox, equipesHbox, relatoriosHbox, logoutHbox;
    @FXML
    private Button dashboardButton, ocorrenciasButton, ambulanciasButton, equipesButton, relatoriosButton;
    @FXML
    private Label bemVindoLabel;

    private List<HBox> navButtons;

    private Navigator navigator;

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
        navigator.setContent(contentArea, Screens.DASHBOARD_OVERVIEW);
    }

    @FXML
    public void handleOcorrenciasClick() {
        setActiveButton(ocorrenciasHbox);
        navigator.setContent(contentArea, Screens.DASHBOARD_OCCORRENCIAS);
    }

    @FXML
    public void handleAmbulanciasClick() {
        setActiveButton(ambulanciasHbox);
        navigator.setContent(contentArea, Screens.DASHBOARD_AMBULANCIAS);
    }

    @FXML
    public void handleProfissionaisClick() {
        setActiveButton(equipesHbox);
        navigator.setContent(contentArea, Screens.DASHBOARD_EQUIPES);
    }

    @FXML
    public void handleRelatoriosClick() {
        setActiveButton(relatoriosHbox);
        navigator.setContent(contentArea, Screens.DASHBOARD_RELATORIOS);
    }

    @FXML
    public void handleLogout() {
        navigator.navigate(Screens.TELA_LOGIN);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
        handleDashboardClick();
    }
}