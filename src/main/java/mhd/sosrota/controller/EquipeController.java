package mhd.sosrota.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Pair;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.service.EquipeService;
import mhd.sosrota.service.ProfissionalService;
import mhd.sosrota.util.AlertUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 17/11/2025
 * @brief Class GerenciarEquipeController
 */
public class EquipeController implements Navigable {
    @FXML
    private TextField nomeTextField, contatoTextField;
    @FXML
    private ComboBox<String> funcaoComboBox;
    @FXML
    private Button cadastrarProfissonalButton, criarEquipeButton;
    @FXML
    private TableView<Profissional> professionalsTable;
    @FXML
    private TableColumn<Profissional, String> colunaNome, colunaFuncao, colunaContato;
    @FXML
    private TableColumn<Profissional, Void> colunaAcoesProfissional;
    @FXML
    private Pagination professionalsPagination, teamsPagination;
    @FXML
    private Label erroLabelProfissionais, erroLabelEquipes;
    @FXML
    private Tab equipesTab;
    @FXML
    private TableView<Equipe> equipesTable;
    @FXML
    private TableColumn<Equipe, String> colunaAmbulancia, colunaMedico, colunaEnfermeiro, colunaCondutor;
    @FXML
    private TableColumn<Equipe, Void> colunaAcoesEquipe;

    @FXML
    private ComboBox<Ambulancia> ambulanciaComboBox;
    @FXML
    private ComboBox<Profissional> medicoComboBox, enfermeiroComboBox, condutorComboBox;

    private final AmbulanciaService ambulanciaService = AppContext.getInstance().getAmbulanciaService();
    private final ProfissionalService profissionalService = AppContext.getInstance().getProfissionalService();
    private final EquipeService equipeService = AppContext.getInstance().getEquipeService();

    private final ObservableList<Equipe> listaEquipes = FXCollections.observableArrayList();
    private final ObservableList<Profissional> profissionais = FXCollections.observableArrayList();

    private final int itensPorPagina = 8;
    private Navigator navigator;

    @FXML
    public void initialize() {
        configurarProfissionaisTab();

        professionalsPagination.currentPageIndexProperty().addListener((_, _, _) ->
                UiUtils.atualizarPaginacao(
                        professionalsPagination,
                        professionalsTable,
                        profissionais,
                        itensPorPagina,
                        () -> professionalsTable.refresh()
                )
        );

        equipesTab.setOnSelectionChanged(_ -> {
            if (equipesTab.isSelected()) {
                configurarEquipesTab();

                teamsPagination.currentPageIndexProperty().addListener((_, _, _) ->
                        UiUtils.atualizarPaginacao(
                                teamsPagination,
                                equipesTable,
                                listaEquipes,
                                itensPorPagina,
                                () -> equipesTable.refresh()
                        )
                );
            }
        });
    }

    private void configurarProfissionaisTab() {
        erroLabelProfissionais.setManaged(false);
        erroLabelProfissionais.setVisible(false);

        cadastrarProfissonalButton.disableProperty().bind(
                nomeTextField.textProperty().isEmpty()
                        .or(contatoTextField.textProperty().isEmpty())
                        .or(funcaoComboBox.valueProperty().isNull())
        );

        funcaoComboBox.getItems().addAll(
                FuncaoProfissional.getNomes()
        );

        configurarTabelaProfissionais();
        carregarProfissionais();
    }

    private void configurarEquipesTab() {
        erroLabelEquipes.setManaged(false);
        erroLabelEquipes.setVisible(false);

        Callback<ListView<Ambulancia>, ListCell<Ambulancia>> ambulanciaFactory =
                UiUtils.comboCellFactory(a -> a.getPlaca() + " (" + a.getBairroBase() + ")" + " - " + a.getTipoAmbulancia().getDescricao());

        ambulanciaComboBox.setCellFactory(ambulanciaFactory);
        medicoComboBox.setCellFactory(UiUtils.comboCellFactory(Profissional::getNome));
        enfermeiroComboBox.setCellFactory(UiUtils.comboCellFactory(Profissional::getNome));
        condutorComboBox.setCellFactory(UiUtils.comboCellFactory(Profissional::getNome));

        ambulanciaComboBox.setButtonCell(ambulanciaFactory.call(null));
        medicoComboBox.setButtonCell(UiUtils.comboCellFactory(Profissional::getNome).call(null));
        enfermeiroComboBox.setButtonCell(UiUtils.comboCellFactory(Profissional::getNome).call(null));
        condutorComboBox.setButtonCell(UiUtils.comboCellFactory(Profissional::getNome).call(null));

        criarEquipeButton.disableProperty().bind(
                ambulanciaComboBox.valueProperty().isNull()
                        .or(enfermeiroComboBox.valueProperty().isNull())
                        .or(condutorComboBox.valueProperty().isNull())
        );

        Task<Pair<List<Ambulancia>, List<Profissional>>> task = new Task<>() {
            @Override
            protected Pair<List<Ambulancia>, List<Profissional>> call() {
                var ambulancias = ambulanciaService.listarDisponiveis();
                var profissionais = profissionalService.listarProfissionaisDisponiveis();
                return new Pair<>(ambulancias, profissionais);
            }

            @Override
            protected void succeeded() {
                var ambulancias = getValue().getKey();
                var profissionais = getValue().getValue();

                var idsAmbulancias = ambulanciaComboBox.getItems().stream()
                        .map(Ambulancia::getId)
                        .collect(java.util.stream.Collectors.toSet());
                ambulancias.stream()
                        .filter(a -> !idsAmbulancias.contains(a.getId()))
                        .forEach(a -> ambulanciaComboBox.getItems().add(a));

                List<Profissional> medicos = profissionais.stream()
                        .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.MEDICO)
                        .toList();

                List<Profissional> enfermeiros = profissionais.stream()
                        .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.ENFERMEIRO)
                        .toList();

                List<Profissional> condutores = profissionais.stream()
                        .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.CONDUTOR)
                        .toList();

                var idsMedicos = medicoComboBox.getItems().stream()
                        .map(Profissional::getId)
                        .collect(java.util.stream.Collectors.toSet());
                medicos.stream()
                        .filter(p -> !idsMedicos.contains(p.getId()))
                        .forEach(p -> medicoComboBox.getItems().add(p));

                var idsEnfermeiros = enfermeiroComboBox.getItems().stream()
                        .map(Profissional::getId)
                        .collect(java.util.stream.Collectors.toSet());
                enfermeiros.stream()
                        .filter(p -> !idsEnfermeiros.contains(p.getId()))
                        .forEach(p -> enfermeiroComboBox.getItems().add(p));

                var idsCondutores = condutorComboBox.getItems().stream()
                        .map(Profissional::getId)
                        .collect(java.util.stream.Collectors.toSet());
                condutores.stream()
                        .filter(p -> !idsCondutores.contains(p.getId()))
                        .forEach(p -> condutorComboBox.getItems().add(p));
            }

            @Override
            protected void failed() {
                mostrarErro(erroLabelEquipes, "Houve um erro ao carregar os dados");
            }
        };

        new Thread(task).start();

        configurarTabelaEquipe();
        carregarEquipe();
    }

    private void configurarTabelaEquipe() {
        colunaAmbulancia.setCellValueFactory(cellData -> {
            var ambulancia = cellData.getValue().getAmbulancia();
            return new SimpleStringProperty(ambulancia != null ? ambulancia.getPlaca() : "-");
        });

        colunaMedico.setCellValueFactory(cellData ->
                obterNomePorFuncao(cellData.getValue(), FuncaoProfissional.MEDICO));

        colunaEnfermeiro.setCellValueFactory(cellData ->
                obterNomePorFuncao(cellData.getValue(), FuncaoProfissional.ENFERMEIRO));

        colunaCondutor.setCellValueFactory(cellData ->
                obterNomePorFuncao(cellData.getValue(), FuncaoProfissional.CONDUTOR));

        colunaAcoesEquipe.setCellFactory(UiUtils.criarColunaAcoes(
                (row) -> {
                    abrirEditarEquipe(row);
                    carregarEquipe();
                },
                (row) -> {
                    equipeService.removerEquipe(row.getId());
                    carregarEquipe();
                },
                "Deletar equipe",
                "Tem certeza que deseja deletar essa equipe?"
        ));
    }

    private void configurarTabelaProfissionais() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaFuncao.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncaoProfissional().getNome()
        ));
        colunaContato.setCellValueFactory(new PropertyValueFactory<>("contato"));

        colunaAcoesProfissional.setCellFactory(UiUtils.criarColunaAcoes(
                (row) -> {
                    abrirEditarProfissional(row);
                    carregarProfissionais();
                },
                (row) -> {
                    profissionalService.deletarProfissional(row.getId());
                    carregarProfissionais();
                },
                "Deletar profissional",
                "Tem certeza que deseja deletar este profissional?"
        ));
    }

    private void carregarProfissionais() {
        Task<ObservableList<Profissional>> task = new Task<>() {
            @Override
            protected ObservableList<Profissional> call() {
                List<Profissional> lista = profissionalService.listarTodosProfissionais();
                if (lista == null || lista.isEmpty()) {
                    professionalsTable.setPlaceholder(new Label("Não há profissionais cadastrados"));
                    return FXCollections.emptyObservableList();
                }
                return FXCollections.observableArrayList(lista);
            }

            @Override
            protected void succeeded() {
                profissionais.setAll(getValue());
                UiUtils.atualizarPaginacao(professionalsPagination, professionalsTable, profissionais, itensPorPagina);
            }

            @Override
            protected void failed() {
                professionalsTable.setPlaceholder(new Label("Erro ao carregar profissionais"));
            }
        };

        new Thread(task).start();
    }

    private void carregarEquipe() {
        Task<ObservableList<Equipe>> task = new Task<>() {
            @Override
            protected ObservableList<Equipe> call() {
                List<Equipe> lista = equipeService.listarEquipes();
                if (lista == null || lista.isEmpty()) {
                    equipesTable.setPlaceholder(new Label("Não há equipes cadastradas"));
                    return FXCollections.emptyObservableList();
                }
                return FXCollections.observableArrayList(lista);
            }

            @Override
            protected void succeeded() {
                listaEquipes.setAll(getValue());
                UiUtils.atualizarPaginacao(teamsPagination, equipesTable, listaEquipes, itensPorPagina);
            }

            @Override
            protected void failed() {
                getException().printStackTrace();
                equipesTable.setPlaceholder(new Label("Erro ao carregar equipes"));
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleClearFields() {
        nomeTextField.clear();
        contatoTextField.clear();
        funcaoComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRegistrarProfissional() {
        erroLabelProfissionais.setVisible(false);
        erroLabelProfissionais.setManaged(false);

        String nome = nomeTextField.getText();
        String email = contatoTextField.getText();
        String funcao = funcaoComboBox.getValue();

        UiUtils.setButtonLoading(cadastrarProfissonalButton, true, "Cadastrar profissional");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                profissionalService.cadastrarProfissional(nome, funcao, email);
                return null;
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(cadastrarProfissonalButton, false, "Cadastrar profissional");
                AlertUtil.showInfo("Sucesso", "Profissional cadastrado com sucesso!");
                handleClearFields();
                carregarProfissionais();
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(cadastrarProfissonalButton, false, "Cadastrar profissional");

                Throwable e = getException();
                if (e instanceof CadastroException) {
                    mostrarErro(erroLabelProfissionais, e.getMessage());
                } else if (e instanceof SQLException) {
                    e.printStackTrace();
                    mostrarErro(erroLabelProfissionais, "Erro no sistema.");
                } else {
                    e.printStackTrace();
                    mostrarErro(erroLabelProfissionais, "Algo deu errado.");
                }
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleCriarEquipe() {
        erroLabelEquipes.setVisible(false);
        erroLabelEquipes.setManaged(false);

        Ambulancia ambulancia = ambulanciaComboBox.getValue();
        Profissional medico = medicoComboBox.getValue();
        Profissional enfermeiro = enfermeiroComboBox.getValue();
        Profissional condutor = condutorComboBox.getValue();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Equipe equipe = new Equipe();
                equipe.setAmbulancia(ambulancia);
                equipe.addProfissional(enfermeiro);
                equipe.addProfissional(condutor);
                if (medico != null) {
                    equipe.addProfissional(medico);
                }
                equipeService.cadastrarEquipe(equipe);
                return null;
            }

            @Override
            protected void succeeded() {
                AlertUtil.showInfo("Sucesso", "Equipe cadastrada com sucesso!");
                handleLimparEquipe();
                carregarEquipe();
            }

            @Override
            protected void failed() {
                Throwable e = getException();
                if (e instanceof CadastroException) {
                    mostrarErro(erroLabelEquipes, e.getMessage());
                } else if (e instanceof SQLException) {
                    e.printStackTrace();
                    mostrarErro(erroLabelEquipes, "Erro no sistema.");
                } else {
                    e.printStackTrace();
                    mostrarErro(erroLabelEquipes, "Algo deu errado.");
                }
            }
        };

        new Thread(task).start();
    }

    private void abrirEditarProfissional(Profissional profissional) {
        AppContext.getInstance().setProfissionalEmEdicao(profissional);
        navigator.showModal(Screens.EDITAR_PROFISSIONAL, "Editar Profissional");
    }

    private void abrirEditarEquipe(Equipe equipe) {
        AppContext.getInstance().setEquipeEmEdicao(equipe);
        navigator.showModal(Screens.EDITAR_EQUIPE, "Editar Equipe", 450.0, 510.0);
    }

    @FXML
    private void handleLimparEquipe() {
        ambulanciaComboBox.getSelectionModel().clearSelection();
        medicoComboBox.getSelectionModel().clearSelection();
        enfermeiroComboBox.getSelectionModel().clearSelection();
        condutorComboBox.getSelectionModel().clearSelection();
    }

    private void mostrarErro(Label label, String mensagem) {
        label.setText(mensagem);
        label.setVisible(true);
        label.setManaged(true);
    }

    private ObservableValue<String> obterNomePorFuncao(Equipe equipe, FuncaoProfissional funcaoDesejada) {
        if (equipe.getProfissionais() == null || equipe.getProfissionais().isEmpty()) {
            return new SimpleStringProperty("-");
        }

        return equipe.getProfissionais().stream()
                .filter(p -> p.getFuncaoProfissional() == funcaoDesejada)
                .findFirst()
                .map(p -> new SimpleStringProperty(p.getNome()))
                .orElse(new SimpleStringProperty("-"));
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
