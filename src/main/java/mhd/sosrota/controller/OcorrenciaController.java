package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    // Lista real de bairros para fazer lookup
    private List<Bairro> bairros;

    @FXML
    public void initialize() {

        // ---- Carrega bairros ----
        bairros = AppContext.getInstance().getGrafoService().obterBairros();

        bairroComboBox.getItems().addAll(
                bairros.stream().map(Bairro::getNome).toList()
        );

        // ---- Carrega gravidade ----
        gravidadeComboBox.getItems().addAll(
                Arrays.stream(GravidadeOcorrencia.values())
                        .map(GravidadeOcorrencia::getDescricao)
                        .toList()
        );

        // ---- Carrega status (opcional, depende da tela) ----
        if (statusComboBox != null) {
            statusComboBox.getItems().addAll(
                    Arrays.stream(StatusOcorrencia.values())
                            .map(StatusOcorrencia::getDescricao)
                            .toList()
            );
        }

        // ---- Validação automática do botão ----
        if (registrarOcorrenciaButton != null) {
            registrarOcorrenciaButton.disableProperty().bind(
                    bairroComboBox.getSelectionModel().selectedItemProperty().isNull()
                            .or(gravidadeComboBox.getSelectionModel().selectedItemProperty().isNull())
                            .or(tipoOcorrenciaTextField.textProperty().isEmpty())
            );
        }
    }

    @FXML
    private void handleClearFields() {
        bairroComboBox.getSelectionModel().clearSelection();
        gravidadeComboBox.getSelectionModel().clearSelection();

        if (statusComboBox != null)
            statusComboBox.getSelectionModel().clearSelection();

        tipoOcorrenciaTextField.clear();
        obsTextArea.clear();
    }

    @FXML
    private void handleRegistrarOcorrencia() {

        // ---- Converter bairro selecionado ----
        String bairroSelecionado = bairroComboBox.getValue();
        Bairro bairro = bairros.stream()
                .filter(b -> b.getNome().equals(bairroSelecionado))
                .findFirst()
                .orElse(null);

        // ---- Converter gravidade descrição → enum real ----
        GravidadeOcorrencia gravidade = Arrays.stream(GravidadeOcorrencia.values())
                .filter(g -> g.getDescricao().equals(gravidadeComboBox.getValue()))
                .findFirst()
                .orElse(null);

        // ---- Converter status se existir combo ----
        StatusOcorrencia status = null;
        if (statusComboBox != null) {
            status = Arrays.stream(StatusOcorrencia.values())
                    .filter(s -> s.getDescricao().equals(statusComboBox.getValue()))
                    .findFirst()
                    .orElse(StatusOcorrencia.ABERTA);
        } else {
            status = StatusOcorrencia.ABERTA; // padrão
        }

        // ---- Criar ocorrência ----
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setBairro(bairro);
        ocorrencia.setTipoOcorrencia(tipoOcorrenciaTextField.getText());
        ocorrencia.setGravidadeOcorrencia(gravidade);
        ocorrencia.setStatusOcorrencia(status);
        ocorrencia.setDataHoraAbertura(LocalDateTime.now());
        ocorrencia.setObservacao(obsTextArea.getText());

        // ---- Persistir ----
        boolean sucesso = AppContext.getInstance()
                .getOcorrenciaService()
                .salvar(ocorrencia);

        if (sucesso) {
            handleClearFields();
            System.out.println("Ocorrência registrada com sucesso!");
        } else {
            System.err.println("Falha ao registrar ocorrência!");
        }
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
