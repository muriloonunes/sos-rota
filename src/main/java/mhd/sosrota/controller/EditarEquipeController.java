package mhd.sosrota.controller;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.enums.TipoAmbulancia;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.service.EquipeService;
import mhd.sosrota.service.ProfissionalService;
import mhd.sosrota.util.AlertUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 02/12/2025
 * @brief Class EditarEquipeController
 */
public class EditarEquipeController implements Navigable {
    @FXML
    private ComboBox<Ambulancia> ambulanciaComboBox;
    @FXML
    private ComboBox<Profissional> medicoComboBox, enfermeiroComboBox, condutorComboBox;
    @FXML
    private Button salvarButton, deletarButton, cancelarButton;
    @FXML
    private Label erroLabel;

    private final AmbulanciaService ambulanciaService =
            AppContext.getInstance().getAmbulanciaService();
    private final ProfissionalService profissionalService =
            AppContext.getInstance().getProfissionalService();
    private final EquipeService equipeService =
            AppContext.getInstance().getEquipeService();

    private Navigator navigator;
    private Equipe equipeEmEdicao;

    private Ambulancia ambulanciaOriginal;
    private Profissional medicoOriginal;
    private Profissional enfermeiroOriginal;
    private Profissional condutorOriginal;

    @FXML
    public void initialize() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        equipeEmEdicao = AppContext.getInstance().getEquipeEmEdicao();

        configurarCombos();
        carregarDadosIniciais();
    }

    private void configurarCombos() {
        Callback<ListView<Ambulancia>, ListCell<Ambulancia>> ambulanciaFactory =
                UiUtils.comboCellFactory(a -> a.getPlaca() + " - " + a.getTipoAmbulancia().getDescricao());

        ambulanciaComboBox.setCellFactory(ambulanciaFactory);
        ambulanciaComboBox.setButtonCell(ambulanciaFactory.call(null));

        medicoComboBox.setCellFactory(UiUtils.comboCellFactory(Profissional::getNome));
        medicoComboBox.setButtonCell(UiUtils.comboCellFactory(Profissional::getNome).call(null));

        enfermeiroComboBox.setCellFactory(UiUtils.comboCellFactory(Profissional::getNome));
        enfermeiroComboBox.setButtonCell(UiUtils.comboCellFactory(Profissional::getNome).call(null));

        condutorComboBox.setCellFactory(UiUtils.comboCellFactory(Profissional::getNome));
        condutorComboBox.setButtonCell(UiUtils.comboCellFactory(Profissional::getNome).call(null));
    }

    private void carregarDadosIniciais() {
        if (equipeEmEdicao == null) {
            mostrarErro("Nenhuma equipe selecionada para edição.");
            desabilitarBotoes();
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                var ambulancias = ambulanciaService.listarAmbulanciaSemEquipe();
                var profissionaisDisponiveis = profissionalService.listarProfissionaisDisponiveis();

                Ambulancia atual = equipeEmEdicao.getAmbulancia();
                if (atual != null && ambulancias.stream().noneMatch(a -> Objects.equals(a.getId(), atual.getId()))) {
                    ambulancias.add(atual);
                }

                List<Profissional> atuais = equipeEmEdicao.getProfissionais();
                for (Profissional p : atuais) {
                    if (profissionaisDisponiveis.stream().noneMatch(dp -> Objects.equals(dp.getId(), p.getId()))) {
                        profissionaisDisponiveis.add(p);
                    }
                }

                var medicos = profissionaisDisponiveis.stream()
                        .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.MEDICO)
                        .collect(Collectors.toList());

                var enfermeiros = profissionaisDisponiveis.stream()
                        .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.ENFERMEIRO)
                        .collect(Collectors.toList());

                var condutores = profissionaisDisponiveis.stream()
                        .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.CONDUTOR)
                        .collect(Collectors.toList());

                updateValue(null);

                ambulanciaComboBox.getItems().setAll(ambulancias);
                medicoComboBox.getItems().setAll(medicos);
                enfermeiroComboBox.getItems().setAll(enfermeiros);
                condutorComboBox.getItems().setAll(condutores);

                return null;
            }

            @Override
            protected void succeeded() {
                preencherCamposComEquipe();
            }

            @Override
            protected void failed() {
                getException().printStackTrace();
                mostrarErro("Erro ao carregar dados da equipe.");
                desabilitarBotoes();
            }
        };

        new Thread(task).start();
    }

    private void preencherCamposComEquipe() {
        if (equipeEmEdicao == null) {
            return;
        }
        if (equipeEmEdicao.getAmbulancia() != null) {
            ambulanciaComboBox.getSelectionModel()
                    .select(equipeEmEdicao.getAmbulancia());
        }
        Profissional medico = equipeEmEdicao.getProfissionais().stream()
                .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.MEDICO)
                .findFirst()
                .orElse(null);

        Profissional enfermeiro = equipeEmEdicao.getProfissionais().stream()
                .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.ENFERMEIRO)
                .findFirst()
                .orElse(null);

        Profissional condutor = equipeEmEdicao.getProfissionais().stream()
                .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.CONDUTOR)
                .findFirst()
                .orElse(null);

        if (medico != null) {
            medicoComboBox.getSelectionModel().select(medico);
        }
        if (enfermeiro != null) {
            enfermeiroComboBox.getSelectionModel().select(enfermeiro);
        }
        if (condutor != null) {
            condutorComboBox.getSelectionModel().select(condutor);
        }

        ambulanciaOriginal = ambulanciaComboBox.getValue();
        medicoOriginal = medicoComboBox.getValue();
        enfermeiroOriginal = enfermeiroComboBox.getValue();
        condutorOriginal = condutorComboBox.getValue();

        var unchangedBinding = Bindings.createBooleanBinding(() -> Objects.equals(ambulanciaComboBox.getValue(), ambulanciaOriginal)
                        && Objects.equals(medicoComboBox.getValue(), medicoOriginal)
                        && Objects.equals(enfermeiroComboBox.getValue(), enfermeiroOriginal)
                        && Objects.equals(condutorComboBox.getValue(), condutorOriginal),
                ambulanciaComboBox.valueProperty(),
                medicoComboBox.valueProperty(),
                enfermeiroComboBox.valueProperty(),
                condutorComboBox.valueProperty()
        );

        salvarButton.disableProperty().bind(unchangedBinding);
    }

    @FXML
    private void handleSalvar() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        Ambulancia ambulancia = ambulanciaComboBox.getValue();
        Profissional medico = medicoComboBox.getValue();
        Profissional enfermeiro = enfermeiroComboBox.getValue();
        Profissional condutor = condutorComboBox.getValue();

        if (ambulancia == null || enfermeiro == null || condutor == null) {
            mostrarErro("Ambulância, enfermeiro e condutor são obrigatórios.");
            return;
        }

        if (ambulancia.getTipoAmbulancia() == TipoAmbulancia.UTI && medico == null) {
            mostrarErro("Uma ambulância do tipo UTI deve ter um médico.");
            return;
        }

        UiUtils.setButtonLoading(salvarButton, true, "Salvar alterações");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                equipeEmEdicao.setAmbulancia(ambulancia);
                equipeEmEdicao.getProfissionais().clear();
                equipeEmEdicao.addProfissional(enfermeiro);
                equipeEmEdicao.addProfissional(condutor);
                if (medico != null) {
                    equipeEmEdicao.addProfissional(medico);
                }

                equipeService.atualizarEquipe(equipeEmEdicao);
                return null;
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(salvarButton, false, "Salvar alterações");
                AlertUtil.showInfo("Sucesso", "Equipe atualizada com sucesso!");
                AppContext.getInstance().setEquipeEmEdicao(null);
                handleCancelar();
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(salvarButton, false, "Salvar alterações");
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
        if (equipeEmEdicao == null) {
            mostrarErro("Nenhuma equipe selecionada.");
            return;
        }

        var result = AlertUtil.showConfirmation(
                "Deletar equipe",
                "Tem certeza que deseja deletar essa equipe?"
        );

        if (result.get() != ButtonType.OK) {
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                equipeService.removerEquipe(equipeEmEdicao.getId());
                return null;
            }

            @Override
            protected void succeeded() {
                AlertUtil.showInfo("Sucesso", "Equipe deletada com sucesso!");
                navigator.closeStage(cancelarButton);
            }

            @Override
            protected void failed() {
                getException().printStackTrace();
                mostrarErro("Erro ao deletar equipe.");
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleCancelar() {
        navigator.closeStage(cancelarButton);
    }

    @FXML
    private void handleLimparMedico() {
        medicoComboBox.getSelectionModel().clearSelection();
        medicoComboBox.setValue(null);

        erroLabel.setVisible(false);
        erroLabel.setManaged(false);
    }

    private void mostrarErro(String mensagem) {
        erroLabel.setText(mensagem);
        erroLabel.setVisible(true);
        erroLabel.setManaged(true);
    }

    private void desabilitarBotoes() {
        salvarButton.setDisable(true);
        deletarButton.setDisable(true);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
