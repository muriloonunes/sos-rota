package mhd.sosrota.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.presentation.model.AmbulanciaRow;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.util.AlertUtil;

import java.sql.SQLException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 29/11/2025
 * @brief Class EditarAmbulanciaController
 */
public class EditarAmbulanciaController {
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

    @FXML
    private void initialize() {
        erroLabel.setManaged(false);
        erroLabel.setVisible(false);

        UiUtils.configurarCamposAmbulancia(placaField, tipoComboBox, statusComboBox, baseComboBox);
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
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);
        String placa = placaField.getText().toUpperCase();
        String tipo = tipoComboBox.getValue();
        String status = statusComboBox.getValue();
        Bairro base = baseComboBox.getValue();

        UiUtils.setButtonLoading(salvarModalButton, true, "Salvar alterações");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.atualizarAmbulancia(placa, status, tipo, base, ambulancia.getId());
                return null;
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(salvarModalButton, false, "Salvar alterações");
                AlertUtil.showInfo("Sucesso", "Dados editados com sucesso!");
                AppContext.getInstance().setAmbulanciaEmEdicao(null);
                handleCancelar();
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(salvarModalButton, false, "Salvar alterações");
                Throwable e = getException();

                if (e instanceof CadastroException) {
                    mostrarErro(e.getMessage());
                } else if (e instanceof SQLException) {
                    e.printStackTrace();
                    mostrarErro("Erro no sistema.");
                } else {
                    e.printStackTrace();
                    mostrarErro("Algo deu errado.");
                }
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleDeletar() {
        var result = AlertUtil.showConfirmation("Deletar ambulância", "Tem certeza que deseja deletar a ambulância?");
        if (result.get() == ButtonType.OK) {
            service.deletarAmbulancia(ambulancia.getId());
            handleCancelar(); //pra fechar o modal de edição
        }
    }

    @FXML
    private void handleCancelar() {
        AppContext.getInstance().setAmbulanciaEmEdicao(null);
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }

    private void mostrarErro(String mensagem) {
        erroLabel.setText(mensagem);
        erroLabel.setVisible(true);
        erroLabel.setManaged(true);
    }
}
