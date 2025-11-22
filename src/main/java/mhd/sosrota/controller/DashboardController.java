package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 22/11/2025
 * @brief Class DashboardController
 */
public class DashboardController implements Navigable {
    @FXML
    private TableView<String> ocorrenciasTableView;
    @FXML
    private TableColumn<String, String> idColumn, localColumn, gravidadeColumn, tipoColumn, statusColumn, aberturaColumn, acoesColumn;

    private Navigator navigator;

    @FXML
    private void criarOcorrencia() {
        navigator.showModal(Screens.CRIAR_OCORRENCIA, "Criar Ocorrência");
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
