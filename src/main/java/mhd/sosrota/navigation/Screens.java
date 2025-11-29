package mhd.sosrota.navigation;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 22/11/2025
 * @brief Enum Screens
 */
public enum Screens {
    TELA_LOGIN("/mhd/sosrota/login_screen.fxml"),
    TELA_APP("/mhd/sosrota/app_sidebar.fxml"),
    DASHBOARD_OVERVIEW("/mhd/sosrota/dashboard_screen.fxml"),
    DASHBOARD_OCCORRENCIAS("/mhd/sosrota/ocorrencias_screen.fxml"),
    CRIAR_OCORRENCIA("/mhd/sosrota/criar_ocorrencia_screen.fxml"),
    DASHBOARD_AMBULANCIAS("/mhd/sosrota/ambulancias_screen.fxml"),
    DASHBOARD_EQUIPES("/mhd/sosrota/equipes_screen.fxml"),
    DASHBOARD_RELATORIOS("/mhd/sosrota/relatorios_screen.fxml"),
    EDITAR_AMBULANCIA("/mhd/sosrota/editar_ambulancia_screen.fxml");
    private final String fxmlPath;

    Screens(String fxmlPath) {
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }
}
