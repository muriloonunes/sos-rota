package mhd.sosrota.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.model.exceptions.DeleteException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.util.AlertUtil;

import java.sql.SQLException;

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
    private Ambulancia ambulancia;
    private Navigator navigator;

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
        tipoComboBox.setValue(ambulancia.getTipoAmbulancia().getDescricao());
        statusComboBox.setValue(ambulancia.getStatusAmbulancia().getDescricao());
        baseComboBox.setValue(ambulancia.getBairroBase());

        salvarModalButton.disableProperty().bind(
                (placaField.textProperty().isEqualTo(ambulancia.getPlaca())
                        .and(tipoComboBox.valueProperty().isEqualTo(ambulancia.getTipoAmbulancia().getDescricao()))
                        .and(statusComboBox.valueProperty().isEqualTo(ambulancia.getStatusAmbulancia().getDescricao()))
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
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.deletarAmbulancia(ambulancia.getId());
                return null;
            }

            @Override
            protected void succeeded() {
                AlertUtil.showInfo("Sucesso", "Ambulância deletada com sucesso.");
                handleCancelar();
            }

            @Override
            protected void failed() {
                Throwable e = getException();
                if (e instanceof DeleteException) {
                    AlertUtil.showError("Erro!", e.getMessage());
                } else {
                    mostrarErro("Erro ao deletar ambulância.");
                }
            }
        };

        var result = AlertUtil.showConfirmation("Deletar ambulância", "Tem certeza que deseja deletar a ambulância?");
        if (result.get() == ButtonType.OK) {
            new Thread(task).start();
        }
    }

    @FXML
    private void handleCancelar() {
        AppContext.getInstance().setAmbulanciaEmEdicao(null);
        navigator.closeStage(cancelarButton);
    }

    private void mostrarErro(String mensagem) {
        erroLabel.setText(mensagem);
        erroLabel.setVisible(true);
        erroLabel.setManaged(true);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
