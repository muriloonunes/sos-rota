package mhd.sosrota.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.service.ProfissionalService;
import mhd.sosrota.util.AlertUtil;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
    private Button cadastrarProfissonalButton;
    @FXML
    private TableView<Profissional> professionalsTable;
    @FXML
    private TableColumn<Profissional, String> colunaNome, colunaFuncao, colunaContato;
    @FXML
    private TableColumn<Profissional, Void> colunaAcoes;
    @FXML
    private Pagination professionalsPagination;
    @FXML
    private Label erroLabelProfissionais;
    @FXML
    private Tab equipesTab;

    @FXML
    private ComboBox<Ambulancia> ambulanciaComboBox;
    @FXML
    private ComboBox<Profissional> medicoComboBox, enfermeiroComboBox, condutorComboBox;

    private final AmbulanciaService ambulanciaService = AppContext.getInstance().getAmbulanciaService();
    private final ProfissionalService service = AppContext.getInstance().getProfissionalService();
    private final ObservableList<Profissional> profissionais = FXCollections.observableArrayList();

    private final int itensPorPagina = 8;
    private Navigator navigator;

    @FXML
    public void initialize() {
        configurarProfissionaisTab();

        equipesTab.setOnSelectionChanged(_ -> {
            if (equipesTab.isSelected()) {
                configurarEquipesTab();
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

        professionalsPagination.currentPageIndexProperty().addListener(
                (_, _, newValue) -> atualizarPag(newValue.intValue()));
    }

    private void configurarEquipesTab() {
        Callback<ListView<Ambulancia>, ListCell<Ambulancia>> ambulanciaFactory =
                cellFactory(a -> a.getPlaca() + " - " + a.getTipoAmbulancia().getDescricao());

        ambulanciaComboBox.setCellFactory(ambulanciaFactory);
        medicoComboBox.setCellFactory(cellFactory(Profissional::getNome));
        enfermeiroComboBox.setCellFactory(cellFactory(Profissional::getNome));
        condutorComboBox.setCellFactory(cellFactory(Profissional::getNome));

        ambulanciaComboBox.setButtonCell(ambulanciaFactory.call(null));
        medicoComboBox.setButtonCell(cellFactory(Profissional::getNome).call(null));
        enfermeiroComboBox.setButtonCell(cellFactory(Profissional::getNome).call(null));
        condutorComboBox.setButtonCell(cellFactory(Profissional::getNome).call(null));


        List<Ambulancia> ambulancias = ambulanciaService.listarDisponiveis();
        ambulanciaComboBox.getItems().addAll(ambulancias);

        List<Profissional> profissionais = service.listarProfissionaisDisponiveis();

        List<Profissional> medicos = profissionais.stream()
                .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.MEDICO)
                .toList();

        List<Profissional> enfermeiros = profissionais.stream()
                .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.ENFERMEIRO)
                .toList();

        List<Profissional> condutores = profissionais.stream()
                .filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.CONDUTOR)
                .toList();

        medicoComboBox.getItems().addAll(medicos);
        enfermeiroComboBox.getItems().addAll(enfermeiros);
        condutorComboBox.getItems().addAll(condutores);
    }

    private void configurarTabelaProfissionais() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaFuncao.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncaoProfissional().getNome()
        ));
        colunaContato.setCellValueFactory(new PropertyValueFactory<>("contato"));

        colunaAcoes.setCellFactory(_ -> new TableCell<>() {
            private final HBox acoesBox = new HBox(10);
            final SVGImage editarImage = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/editar.svg"))).scaleTo(12);
            final SVGImage deleteImage = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/deletar.svg"))).scaleTo(12);
            private final Button editarButton = new Button();
            private final Button deletarButton = new Button();

            {
                editarButton.setGraphic(editarImage);
                deletarButton.setGraphic(deleteImage);

                editarButton.getStyleClass().add("btn-primary");
                deletarButton.getStyleClass().add("btn-ocorrencia");

                editarButton.setOnAction(_ -> {
                    Profissional row = getTableView().getItems().get(getIndex());
                    abrirEditarProfissional(row);
                    carregarProfissionais();
                });

                deletarButton.setOnAction(_ -> {
                    var result = AlertUtil.showConfirmation("Deletar profissional", "Tem certeza que deseja deletar este profissional?");
                    if (result.get() == ButtonType.OK) {
                        Profissional profissional = getTableView().getItems().get(getIndex());
                        service.deletarProfissional(profissional.getId());
                    }
                    carregarProfissionais();
                });

                acoesBox.getChildren().addAll(editarButton, deletarButton);
                acoesBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(acoesBox);
                }
            }
        });
    }

    @FXML
    private void carregarProfissionais() {
        Task<ObservableList<Profissional>> task = new Task<>() {
            @Override
            protected ObservableList<Profissional> call() {
                List<Profissional> lista = service.listarTodosProfissionais();
                if (lista == null || lista.isEmpty()) {
                    professionalsTable.setPlaceholder(new Label("Não há profissionais cadastrados"));
                    return FXCollections.emptyObservableList();
                }
                return FXCollections.observableArrayList(lista);
            }

            @Override
            protected void succeeded() {
                profissionais.setAll(getValue());
                atualizarTotalDePaginas();
                atualizarPag(0);
            }

            @Override
            protected void failed() {
                professionalsTable.setPlaceholder(new Label("Erro ao carregar profissionais"));
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
    private void handleRegisterProfessional() {
        erroLabelProfissionais.setVisible(false);
        erroLabelProfissionais.setManaged(false);

        String nome = nomeTextField.getText();
        String email = contatoTextField.getText();
        String funcao = funcaoComboBox.getValue();

        UiUtils.setButtonLoading(cadastrarProfissonalButton, true, "Cadastrar profissional");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.cadastrarProfissional(nome, funcao, email);
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
                    mostrarErroProfissionais(e.getMessage());
                } else if (e instanceof SQLException) {
                    e.printStackTrace();
                    mostrarErroProfissionais("Erro no sistema.");
                } else {
                    e.printStackTrace();
                    mostrarErroProfissionais("Algo deu errado.");
                }
            }
        };

        new Thread(task).start();
    }

    private void abrirEditarProfissional(Profissional profissional) {
        AppContext.getInstance().setProfissionalEmEdicao(profissional);
        navigator.showModal(Screens.EDITAR_PROFISSIONAL, "Editar Profissional");
    }

    private void atualizarTotalDePaginas() {
        if (profissionais.isEmpty()) {
            professionalsPagination.setPageCount(1);
            return;
        }
        int totalItens = profissionais.size();
        int totalPaginas = (int) Math.ceil((double) totalItens / itensPorPagina);
        professionalsPagination.setPageCount(totalPaginas);
    }

    private void atualizarPag(int indicePagina) {
        if (profissionais.isEmpty()) {
            professionalsTable.setItems(FXCollections.emptyObservableList());
            return;
        }

        if (indicePagina >= professionalsPagination.getPageCount()) {
            indicePagina = professionalsPagination.getPageCount() - 1;
        }

        int fromIndex = indicePagina * itensPorPagina;
        int toIndex = Math.min(fromIndex + itensPorPagina, profissionais.size());

        List<Profissional> paginaAtual = profissionais.subList(fromIndex, toIndex);
        professionalsTable.setItems(FXCollections.observableArrayList(paginaAtual));
    }

    @FXML
    private void handleClearTeamFields() {

    }

    @FXML
    private void handleSaveTeam() {

    }

    private void mostrarErroProfissionais(String mensagem) {
        erroLabelProfissionais.setText(mensagem);
        erroLabelProfissionais.setVisible(true);
        erroLabelProfissionais.setManaged(true);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }

    private <T> Callback<ListView<T>, ListCell<T>> cellFactory(Function<T, String> extractor) {
        return lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : extractor.apply(item));
            }
        };
    }

}
