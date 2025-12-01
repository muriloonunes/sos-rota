package mhd.sosrota.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.util.AlertUtil;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class CadastrarAmbulanciaController
 */
public class AmbulanciaController implements Navigable {
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
    private TableView<Ambulancia> tabelaAmbulancias;
    @FXML
    private TableColumn<Ambulancia, String> colunaPlaca, colunaTipo, colunaStatus, colunaBase;
    @FXML
    private TableColumn<Ambulancia, Void> colunaAcoes;
    @FXML
    private Pagination paginacaoAmbulancias;

    private final AmbulanciaService service = AppContext.getInstance().getAmbulanciaService();
    private final ObservableList<Ambulancia> ambulancias = FXCollections.observableArrayList();

    private int itensPorPagina = 8;
    private Navigator navigator;

    @FXML
    private void initialize() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        configurarTabela();

        carregarAmbulancias();

        UiUtils.configurarCamposAmbulancia(placaTextField, tipoComboBox, statusComboBox, baseComboBox);

        cadastrarAmbulanciaButton.disableProperty().bind(
                (placaTextField.textProperty().isEmpty()
                        .or(tipoComboBox.valueProperty().isNull())
                        .or(statusComboBox.valueProperty().isNull())
                        .or(baseComboBox.valueProperty().isNull()))
                        .or(placaTextField.textProperty().length().lessThan(7))
        );

        paginacaoAmbulancias.currentPageIndexProperty().addListener(
                (_, _, newValue) -> atualizarPag(newValue.intValue()));

        tabelaAmbulancias.heightProperty().addListener(
                (_, _, newHeight) -> recalcularItens(newHeight.doubleValue()));
    }

    private void configurarTabela() {
        colunaPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colunaBase.setCellValueFactory(new PropertyValueFactory<>("bairroBase"));

        colunaTipo.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTipoAmbulancia().getDescricao()
        ));
        colunaStatus.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStatusAmbulancia().getDescricao()
        ));

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
                    Ambulancia row = getTableView().getItems().get(getIndex());
                    abrirEditarAmbulancias(row);
                    carregarAmbulancias();
                });

                deletarButton.setOnAction(_ -> {
                    var result = AlertUtil.showConfirmation("Deletar ambulância", "Tem certeza que deseja deletar a ambulância?");
                    if (result.get() == ButtonType.OK) {
                        Ambulancia row = getTableView().getItems().get(getIndex());
                        service.deletarAmbulancia(row.getId());
                    }
                    carregarAmbulancias();
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
    private void carregarAmbulancias() {
        Task<ObservableList<Ambulancia>> task = new Task<>() {
            @Override
            protected ObservableList<Ambulancia> call() {
                List<Ambulancia> ambulancias = service.listarTodasAmbulancias();
                if (ambulancias == null) {
                    throw new IllegalStateException("Erro ao carregar");
                }
                if (ambulancias.isEmpty()) {
                    tabelaAmbulancias.setPlaceholder(new Label("Não há conteúdo na tabela"));
                }
                return FXCollections.observableArrayList(ambulancias);
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

        String placa = placaTextField.getText().toUpperCase();
        String tipoDesc = tipoComboBox.getValue();
        String statusDesc = statusComboBox.getValue();
        Bairro base = baseComboBox.getValue();

        UiUtils.setButtonLoading(cadastrarAmbulanciaButton, true, "Registrar Ambulância");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.cadastrarAmbulancia(placa, statusDesc, tipoDesc, base);
                return null;
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(cadastrarAmbulanciaButton, false, "Registrar Ambulância");
                AlertUtil.showInfo("Sucesso", "Ambulância registrada com sucesso!");
                handleClearFields();

                carregarAmbulancias();
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(cadastrarAmbulanciaButton, false, "Registrar Ambulância");
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

    private void mostrarErro(String mensagem) {
        erroLabel.setText(mensagem);
        erroLabel.setVisible(true);
        erroLabel.setManaged(true);
    }

    private void abrirEditarAmbulancias(Ambulancia row) {
        AppContext.getInstance().setAmbulanciaEmEdicao(row);
        navigator.showModal(Screens.EDITAR_AMBULANCIA, "Editar Ambulância");
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

        List<Ambulancia> paginaAtual = ambulancias.subList(fromIndex, toIndex);
        tabelaAmbulancias.setItems(FXCollections.observableArrayList(paginaAtual));
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
