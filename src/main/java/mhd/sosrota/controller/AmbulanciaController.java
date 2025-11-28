package mhd.sosrota.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.presentation.model.AmbulanciaRow;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.util.AlertUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class CadastrarAmbulanciaController
 */
public class AmbulanciaController {
    @FXML
    private ComboBox<String> tipoComboBox, statusComboBox;
    @FXML
    private ComboBox<Bairro> baseComboBox;
    @FXML
    private TextField placaTextField;
    @FXML
    private Button cadastrarAmbulanciaButton;
    @FXML
    private Label erroLabel;
    @FXML
    private TableView<AmbulanciaRow> tabelaAmbulancias;
    @FXML
    private TableColumn<AmbulanciaRow, String> colunaPlaca, colunaTipo, colunaStatus, colunaBase;
    @FXML
    private Pagination paginacaoAmbulancias;

    private final AmbulanciaService service = AppContext.getInstance().getAmbulanciaService();
    private ObservableList<AmbulanciaRow> ambulancias = FXCollections.observableArrayList();

    private int itensPorPagina = 8;

    @FXML
    private void initialize() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        colunaPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colunaTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colunaBase.setCellValueFactory(new PropertyValueFactory<>("bairroBase"));

        carregarAmbulancias();

        List<Bairro> bases = AppContext.getInstance().getGrafoService().obterBairros().stream().filter(
                Bairro::temBase
        ).toList();

        tipoComboBox.getItems().addAll(
                Arrays.stream(TipoAmbulancia.values())
                        .map(TipoAmbulancia::getDescricao)
                        .toList()
        );

        statusComboBox.getItems().addAll(
                Arrays.stream(StatusAmbulancia.values())
                        .map(StatusAmbulancia::getDescricao)
                        .toList()
        );

        baseComboBox.getItems().addAll(
                bases
        );

        placaTextField.textProperty().addListener((_, _, newValue) -> {
            if (placaTextField.getText().length() > 7) {
                placaTextField.setText(newValue.substring(0, 7));
            }
        });

        cadastrarAmbulanciaButton.disableProperty().bind(
                placaTextField.textProperty().isEmpty()
                        .or(tipoComboBox.valueProperty().isNull())
                        .or(statusComboBox.valueProperty().isNull())
                        .or(baseComboBox.valueProperty().isNull())
        );

        paginacaoAmbulancias.currentPageIndexProperty().addListener(
                (_, _, newValue) -> atualizarPag(newValue.intValue()));

        tabelaAmbulancias.heightProperty().addListener(
                (_, _, newHeight) -> recalcularItens(newHeight.doubleValue()));

        //TODO terminar a coluna de ações pra completar o CRUD de ambulancias
    }

    @FXML
    private void carregarAmbulancias() {
        Task<ObservableList<AmbulanciaRow>> task = new Task<>() {
            @Override
            protected ObservableList<AmbulanciaRow> call() {
                List<Ambulancia> ambulancias = service.listarTodasAmbulancias();
                if (ambulancias == null) {
                    throw new IllegalStateException("Erro ao carregar");
                }
                return FXCollections.observableArrayList(
                        ambulancias.stream().map(AmbulanciaRow::new).toList()
                );
            }

            @Override
            protected void succeeded() {
                ambulancias.setAll(getValue());
                atualizarTotalDePaginas();
                atualizarPag(0);
                Platform.runLater(() -> recalcularItens(tabelaAmbulancias.getHeight()));
            }

            @Override
            protected void failed() {
                tabelaAmbulancias.setPlaceholder(new Label("Houve um erro ao carregar as ambulâncias"));
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleClearFields() {
        placaTextField.clear();
        tipoComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        baseComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRegistrarAmbulancia() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        String placa = placaTextField.getText();
        String tipoDesc = tipoComboBox.getValue();
        String statusDesc = statusComboBox.getValue();
        Bairro base = baseComboBox.getValue();

        setLoading(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.cadastrarAmbulancia(placa, statusDesc, tipoDesc, base);
                return null;
            }

            @Override
            protected void succeeded() {
                setLoading(false);
                AlertUtil.showInfo("Sucesso", "Ambulância registrada com sucesso!");
                handleClearFields();

                carregarAmbulancias();
            }

            @Override
            protected void failed() {
                setLoading(false);
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

    private void setLoading(boolean loading) {
        if (loading) {
            ProgressIndicator pi = new ProgressIndicator();
            pi.setPrefSize(16, 16);

            cadastrarAmbulanciaButton.setGraphic(pi);
            cadastrarAmbulanciaButton.setText(null);
            cadastrarAmbulanciaButton.setMouseTransparent(true);
            cadastrarAmbulanciaButton.setFocusTraversable(true);
        } else {
            cadastrarAmbulanciaButton.setGraphic(null);
            cadastrarAmbulanciaButton.setText("Registrar Ambulância");
            cadastrarAmbulanciaButton.setMouseTransparent(false);
            cadastrarAmbulanciaButton.setFocusTraversable(false);
        }
    }

    private void mostrarErro(String mensagem) {
        erroLabel.setText(mensagem);
        erroLabel.setVisible(true);
        erroLabel.setManaged(true);
    }

    private void recalcularItens(double alturaTabela) {
        if (alturaTabela <= 0) return;
        int novosItensPorPagina = (int) Math.floor((alturaTabela - 25) / 25);
        if (novosItensPorPagina < 1) novosItensPorPagina = 1;
        if (this.itensPorPagina != novosItensPorPagina) {
            this.itensPorPagina = novosItensPorPagina;

            atualizarTotalDePaginas();

            atualizarPag(paginacaoAmbulancias.getCurrentPageIndex());
        }
    }

    private void atualizarTotalDePaginas() {
        if (ambulancias.isEmpty()) {
            paginacaoAmbulancias.setPageCount(1);
            return;
        }

        int totalItens = ambulancias.size();
        int totalPaginas = (int) Math.ceil((double) totalItens / itensPorPagina);

        paginacaoAmbulancias.setPageCount(totalPaginas);
    }

    private void atualizarPag(int indicePagina) {
        if (ambulancias.isEmpty()) {
            tabelaAmbulancias.setItems(FXCollections.emptyObservableList());
            return;
        }

        if (indicePagina >= paginacaoAmbulancias.getPageCount()) {
            indicePagina = paginacaoAmbulancias.getPageCount() - 1;
        }

        int fromIndex = indicePagina * itensPorPagina;
        int toIndex = Math.min(fromIndex + itensPorPagina, ambulancias.size());

        List<AmbulanciaRow> paginaAtual = ambulancias.subList(fromIndex, toIndex);
        tabelaAmbulancias.setItems(FXCollections.observableArrayList(paginaAtual));
    }
}
