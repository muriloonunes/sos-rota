package mhd.sosrota.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.presentation.model.ProfissionalRow;
import mhd.sosrota.service.ProfissionalService;
import mhd.sosrota.util.AlertUtil;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.util.List;
import java.util.Objects;

/**
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
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
    private TableView<ProfissionalRow> professionalsTable;

    @FXML
    private TableColumn<ProfissionalRow, String> colunaNome, colunaFuncao, colunaContato;

    @FXML
    private TableColumn<ProfissionalRow, Void> colunaAcoes;

    @FXML
    private Pagination professionalsPagination;

    private final ProfissionalService service = AppContext.getInstance().getProfissionalService();
    private final ObservableList<ProfissionalRow> profissionais = FXCollections.observableArrayList();

    private int itensPorPagina = 8;

    @FXML
    public void initialize() {
        cadastrarButton.disableProperty().bind(
                nomeTextField.textProperty().isEmpty()
                        .or(contatoTextField.textProperty().isEmpty())
                        .or(funcaoComboBox.valueProperty().isNull())
        );

        funcaoComboBox.getItems().addAll(
                FuncaoProfissional.getNomes()
        );

        configurarTabela();
        carregarProfissionais();

        professionalsPagination.currentPageIndexProperty().addListener(
                (_, _, newValue) -> atualizarPag(newValue.intValue()));
    }

    private void configurarTabela() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaFuncao.setCellValueFactory(new PropertyValueFactory<>("funcao"));
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
                    ProfissionalRow row = getTableView().getItems().get(getIndex());
                    abrirEditarProfissional(row);
                    carregarProfissionais();
                });

                deletarButton.setOnAction(_ -> {
                    var result = AlertUtil.showConfirmation("Deletar profissional", "Tem certeza que deseja deletar este profissional?");
                    if (result.get() == ButtonType.OK) {
                        ProfissionalRow profissional = getTableView().getItems().get(getIndex());
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
        Task<ObservableList<ProfissionalRow>> task = new Task<>() {
            @Override
            protected ObservableList<ProfissionalRow> call() {
                List<Profissional> lista = service.listarTodosProfissionais();
                if (lista == null || lista.isEmpty()) {
                    professionalsTable.setPlaceholder(new Label("Não há profissionais cadastrados"));
                    return FXCollections.emptyObservableList();
                }
                return FXCollections.observableArrayList(lista.stream().map(ProfissionalRow::new).toList());
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
        String nome = nomeTextField.getText();
        String email = contatoTextField.getText();
        String funcao = funcaoComboBox.getValue();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.cadastrarProfissional(nome, funcao, email);
                return null;
            }

            @Override
            protected void succeeded() {
                AlertUtil.showInfo("Sucesso", "Profissional cadastrado com sucesso!");
                handleClearFields();
                carregarProfissionais();
            }

            @Override
            protected void failed() {
                Throwable e = getException();
                if (e instanceof CadastroException) {
                    AlertUtil.showError("Erro", e.getMessage());
                } else {
                    e.printStackTrace();
                    AlertUtil.showError("Erro", "Algo deu errado ao cadastrar profissional.");
                }
            }
        };

        new Thread(task).start();

    }

    private void abrirEditarProfissional(ProfissionalRow profissional) {
        AppContext.getInstance().setProfissionalEmEdicao(profissional.toProfissional());
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

        List<ProfissionalRow> paginaAtual = profissionais.subList(fromIndex, toIndex);
        professionalsTable.setItems(FXCollections.observableArrayList(paginaAtual));
    }

    @FXML
    private void handleClearTeamFields() {

    }

    @FXML
    private void handleSaveTeam() {

    }
}
