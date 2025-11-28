package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mhd.sosrota.model.enums.FuncaoProfissional;

import java.util.Arrays;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class GerenciarEquipeController
 */
public class EquipeController {
    @FXML
    private TextField nomeTextField, contatoTextField;

    @FXML
    private ComboBox<String> funcaoComboBox;

    @FXML
    private Button cadastrarButton;

    @FXML
    public void initialize() {
        cadastrarButton.disableProperty().bind(
                nomeTextField.textProperty().isEmpty()
                        .or(contatoTextField.textProperty().isEmpty())
                        .or(funcaoComboBox.valueProperty().isNull())
        );

        funcaoComboBox.getItems().addAll(
                Arrays.stream(FuncaoProfissional.values())
                        .map(FuncaoProfissional::getNome)
                        .toList()
        );
    }

    @FXML
    private void handleClearFields() {
        nomeTextField.clear();
        contatoTextField.clear();
        funcaoComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRegisterProfessional() {

    }

    @FXML
    private void handleClearTeamFields() {

    }

    @FXML
    private void handleSaveTeam() {

    }
}
