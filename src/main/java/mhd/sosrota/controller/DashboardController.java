package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 22/11/2025
 * @brief Class DashboardController
 */
public class DashboardController {
    @FXML
    private TableView<String> ocorrenciasTableView;
    @FXML
    private TableColumn<String, String> idColumn, localColumn, gravidadeColumn, tipoColumn, statusColumn, aberturaColumn, acoesColumn;
}
