package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class CadastrarOcorrenciaController
 */
public class OcorrenciaController implements Navigable {
    @FXML
    private ComboBox<String> bairroComboBox, gravidadeComboBox, statusComboBox;
    @FXML
    private TextField tipoOcorrenciaTextField;
    @FXML
    private TextArea obsTextArea;
    @FXML
    private Button registrarOcorrenciaButton;

    private Navigator navigator;

    @FXML
    public void initialize() {
        List<Bairro> bairros = AppContext.getInstance().getGrafoService().obterBairros();
        bairroComboBox.getItems().addAll(bairros.stream()
                .map(Bairro::getNome)
                .toList());

        gravidadeComboBox.getItems().addAll(
                Arrays.stream(GravidadeOcorrencia.values())
                        .map(GravidadeOcorrencia::getDescricao)
                        .toList()
        );

        if (statusComboBox != null) {
            statusComboBox.getItems().addAll(
                    Arrays.stream(StatusOcorrencia.values())
                            .map(StatusOcorrencia::getDescricao)
                            .toList()
            );
        }

        if (registrarOcorrenciaButton != null) {
            registrarOcorrenciaButton.disableProperty().bind(
                    bairroComboBox.getSelectionModel().selectedItemProperty().isNull()
                            .or(gravidadeComboBox.getSelectionModel().selectedItemProperty().isNull()).or(
                                    tipoOcorrenciaTextField.textProperty().isEmpty()
                            )
            );
        }
    }

    @FXML
    private void handleClearFields() {
        bairroComboBox.getSelectionModel().clearSelection();
        gravidadeComboBox.getSelectionModel().clearSelection();
        if (statusComboBox != null) {
            statusComboBox.getSelectionModel().clearSelection();
        }
        if (tipoOcorrenciaTextField != null) {
            tipoOcorrenciaTextField.clear();
            obsTextArea.clear();
        }
    }

    @FXML
    private void handleRegistrarOcorrencia() {

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
