package mhd.sosrota.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.OcorrenciaService;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CriarOcorrenciaController implements Navigable {
    @FXML
    private ComboBox<String> bairroComboBox, gravidadeComboBox;
    @FXML
    private TextField tipoOcorrenciaTextField;
    @FXML
    private TextArea obsTextArea;
    @FXML
    private Button registrarOcorrenciaButton;
    @FXML
    private Label erroLabel;
    @FXML
    private Text tituloTela;

    private List<Bairro> bairros;
    private final OcorrenciaService service = AppContext.getInstance().getOcorrenciaService();
    private Ocorrencia ocorrenciaEmEdicao;
    private Navigator navigator;

    public void initialize() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        bairros = AppContext.getInstance().getGrafoService().obterBairros();

        bairroComboBox.getItems().addAll(
                bairros.stream().map(Bairro::getNome).toList()
        );

        gravidadeComboBox.getItems().addAll(
                Arrays.stream(GravidadeOcorrencia.values())
                        .map(GravidadeOcorrencia::getDescricao)
                        .toList()
        );

        this.ocorrenciaEmEdicao = AppContext.getInstance().getOcorrenciaEmEdicao();
        if (this.ocorrenciaEmEdicao != null) {
            preencherCamposEdicao();
        }

        AppContext.getInstance().setOcorrenciaEmEdicao(null);

        registrarOcorrenciaButton.disableProperty().bind(
                bairroComboBox.getSelectionModel().selectedItemProperty().isNull()
                        .or(gravidadeComboBox.getSelectionModel().selectedItemProperty().isNull())
                        .or(tipoOcorrenciaTextField.textProperty().isEmpty())
        );
    }

    private void preencherCamposEdicao() {
        tituloTela.setText("Editar Ocorrência");
        registrarOcorrenciaButton.setText("Salvar alterações");

        tipoOcorrenciaTextField.setText(ocorrenciaEmEdicao.getTipoOcorrencia());
        obsTextArea.setText(ocorrenciaEmEdicao.getObservacao());
        gravidadeComboBox.getSelectionModel().select(ocorrenciaEmEdicao.getGravidadeOcorrencia().getDescricao());
        if (ocorrenciaEmEdicao.getBairro() != null) {
            bairroComboBox.getSelectionModel().select(ocorrenciaEmEdicao.getBairro().getNome());
        }
    }

    @FXML
    private void handleRegistrarOcorrencia() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        String bairroSelecionado = bairroComboBox.getValue();
        Bairro bairro = bairros.stream()
                .filter(b -> b.getNome().equals(bairroSelecionado))
                .findFirst()
                .orElse(null);

        GravidadeOcorrencia gravidade = Arrays.stream(GravidadeOcorrencia.values())
                .filter(g -> g.getDescricao().equals(gravidadeComboBox.getValue()))
                .findFirst()
                .orElse(null);

        UiUtils.setButtonLoading(registrarOcorrenciaButton, true, "Registrar ocorrência");

        Ocorrencia ocorrencia;
        ocorrencia = Objects.requireNonNullElseGet(this.ocorrenciaEmEdicao, Ocorrencia::new);

        ocorrencia.setBairro(bairro);
        ocorrencia.setTipoOcorrencia(tipoOcorrenciaTextField.getText());
        ocorrencia.setGravidadeOcorrencia(gravidade);
        ocorrencia.setStatusOcorrencia(StatusOcorrencia.ABERTA);
        ocorrencia.setObservacao(obsTextArea.getText());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.salvar(ocorrencia);
                return null;
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(registrarOcorrenciaButton, false, "Registrar ocorrência");
                handleClearFields();
                navigator.closeStage(registrarOcorrenciaButton);
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(registrarOcorrenciaButton, false, "Registrar ocorrência");
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
    private void handleClearFields() {
        bairroComboBox.getSelectionModel().clearSelection();
        gravidadeComboBox.getSelectionModel().clearSelection();

        tipoOcorrenciaTextField.clear();
        obsTextArea.clear();
    }

    private void mostrarErro(String erro) {
        erroLabel.setText(erro);
        erroLabel.setManaged(true);
        erroLabel.setVisible(true);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
