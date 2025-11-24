package mhd.sosrota.controller;

import javafx.fxml.FXML;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class CadastrarOcorrenciaController
 */
public class OcorrenciaController implements Navigable {
    private Navigator navigator;

    @FXML
    private void handleClearFields() {

    }

    @FXML
    private void handleRegisterOccurrence() {

    }

    @FXML
    private void criarOcorrencia() {
        navigator.showModal(Screens.CRIAR_OCORRENCIA, "Criar Ocorrência");
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
