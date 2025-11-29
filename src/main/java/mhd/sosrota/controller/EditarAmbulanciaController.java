package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.presentation.AmbulanciaSetup;
import mhd.sosrota.presentation.model.AmbulanciaRow;
import mhd.sosrota.service.AmbulanciaService;

import static java.lang.IO.println;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 29/11/2025
 * @brief Class EditarAmbulanciaController
 */
public class EditarAmbulanciaController implements Navigable {
    @FXML
    private TextField placaField;
    @FXML
    private ComboBox<String> tipoComboBox, statusComboBox;
    @FXML
    private ComboBox<Bairro> baseComboBox;
    @FXML
    private Label erroLabel;
    @FXML
    private Button salvarModalButton, cancelarButton;

    private final AmbulanciaService service = AppContext.getInstance().getAmbulanciaService();
    private AmbulanciaRow ambulancia;
    private Navigator navigator;

    @FXML
    private void initialize() {
        erroLabel.setManaged(false);
        erroLabel.setVisible(false);

        AmbulanciaSetup.configurarCampos(placaField, tipoComboBox, statusComboBox, baseComboBox);
        preencherCampos();
    }

    private void preencherCampos() {
        this.ambulancia = AppContext.getInstance().getAmbulanciaEmEdicao();
        placaField.setText(ambulancia.getPlaca());
        tipoComboBox.setValue(ambulancia.getTipo());
        statusComboBox.setValue(ambulancia.getStatus());
        baseComboBox.setValue(ambulancia.getBairroBase());

        salvarModalButton.disableProperty().bind(
                (placaField.textProperty().isEqualTo(ambulancia.getPlaca())
                        .and(tipoComboBox.valueProperty().isEqualTo(ambulancia.getTipo()))
                        .and(statusComboBox.valueProperty().isEqualTo(ambulancia.getStatus()))
                        .and(baseComboBox.valueProperty().isEqualTo(ambulancia.getBairroBase())))
                        .or(placaField.textProperty().length().lessThan(7))
        );
    }

    @FXML
    private void handleSalvar() {
        //TODO salvar as alteracoes e atualizar a lista na outra tela
    }

    @FXML
    private void handleDeletar() {
        println(service.deletarAmbulancia(ambulancia.toEntity()));
        //TODO checar pq nao deleta (retorna false)
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
