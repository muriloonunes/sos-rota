package mhd.sosrota.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.OcorrenciaService;
import mhd.sosrota.util.AlertUtil;
import org.girod.javafx.svgimage.SVGLoader;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OcorrenciaController implements Navigable {
    @FXML
    private ComboBox<String> bairroComboBox, gravidadeComboBox, statusComboBox;
    @FXML
    private TableView<Ocorrencia> tabelaOcorrencias;
    @FXML
    private TableColumn<Ocorrencia, Long> idColumn;
    @FXML
    private TableColumn<Ocorrencia, String> bairroColumn, gravidadeColumn, statusColumn, tipoColumn;
    @FXML
    private TableColumn<Ocorrencia, OffsetDateTime> aberturaColumn, slaColumn;
    @FXML
    private TableColumn<Ocorrencia, Void> acoesColumn;

    private Navigator navigator;

    private final OcorrenciaService service = AppContext.getInstance().getOcorrenciaService();
    private final ObservableList<Ocorrencia> ocorrencias = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        List<Bairro> bairros = AppContext.getInstance().getGrafoService().obterBairros();

        bairroComboBox.getItems().addAll(
                bairros.stream().map(Bairro::getNome).toList()
        );

        gravidadeComboBox.getItems().addAll(
                Arrays.stream(GravidadeOcorrencia.values())
                        .map(GravidadeOcorrencia::getDescricao)
                        .toList()
        );

        statusComboBox.getItems().addAll(
                Arrays.stream(StatusOcorrencia.values())
                        .map(StatusOcorrencia::getDescricao)
                        .toList()
        );

        configurarTabela();
        carregarOcorrencias();
        Timeline relogio = new Timeline(new KeyFrame(
                javafx.util.Duration.seconds(1), _ -> tabelaOcorrencias.refresh())
        );
        relogio.setCycleCount(Timeline.INDEFINITE);
        relogio.play();
    }

    private void configurarTabela() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bairroColumn.setCellValueFactory(new PropertyValueFactory<>("bairro"));

        gravidadeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getGravidadeOcorrencia().getDescricao()
        ));

        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStatusOcorrencia().getDescricao()
        ));

        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipoOcorrencia"));
        aberturaColumn.setCellValueFactory(new PropertyValueFactory<>("dataHoraAbertura"));
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        aberturaColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(OffsetDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(formatador.format(item));
                }
            }
        });

        slaColumn.setCellValueFactory(new PropertyValueFactory<>("dataHoraAbertura"));
        slaColumn.setCellFactory(UiUtils.criarSlaCellFactory());

        acoesColumn.setCellFactory(_ -> new TableCell<>() {
            private final HBox acoesBox = new HBox(10);

            private final Button editarButton = new Button();
            private final Button cancelarButton = new Button();
            private final Button despacharButton = new Button();
            private final Button deletarButton = new Button();
            private final Button detalhesButton = new Button();

            {
                editarButton.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/editar.svg"))).scaleTo(12));
                deletarButton.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/deletar.svg"))).scaleTo(12));
                despacharButton.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/rota.svg"))).scaleTo(12));
                cancelarButton.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/cancelar.svg"))).scaleTo(12));
                detalhesButton.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/eye.svg"))).scaleTo(12));

                despacharButton.getStyleClass().add("btn-primary");
                editarButton.getStyleClass().add("btn-primary");
                detalhesButton.getStyleClass().add("btn-primary");
                deletarButton.getStyleClass().add("btn-ocorrencia");
                cancelarButton.getStyleClass().add("btn-ocorrencia");

                detalhesButton.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    abrirDetalhes(ocorrencia);
                });

                despacharButton.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    abrirDespachar(ocorrencia);
                });

                editarButton.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    abrirEditarOcorrencia(ocorrencia);
                });

                cancelarButton.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    cancelarOcorrencia(ocorrencia);
                });

                deletarButton.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    deletarOcorrencia(ocorrencia);
                });

                acoesBox.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0) {
                    setGraphic(null);
                    return;
                }

                Ocorrencia oc = getTableView().getItems().get(getIndex());
                if (oc == null) {
                    setGraphic(null);
                    return;
                }

                acoesBox.getChildren().clear();

                if (oc.getStatusOcorrencia() == StatusOcorrencia.CONCLUIDA
                        || oc.getStatusOcorrencia() == StatusOcorrencia.CANCELADA) {
                    acoesBox.getChildren().add(detalhesButton);
                } else {
                    acoesBox.getChildren().addAll(
                            despacharButton, editarButton, cancelarButton, deletarButton
                    );
                }
                boolean isAberta = oc.getStatusOcorrencia() == StatusOcorrencia.ABERTA;

                despacharButton.setDisable(!isAberta);

                boolean isFinalizada = oc.getStatusOcorrencia() == StatusOcorrencia.CONCLUIDA
                        || oc.getStatusOcorrencia() == StatusOcorrencia.CANCELADA;

                editarButton.setDisable(isFinalizada);
                cancelarButton.setDisable(isFinalizada);

                setGraphic(acoesBox);
            }
        });
    }

    @FXML
    private void handleAplicarFiltros() {
        String bairroSelecionado = bairroComboBox.getValue();
        String gravidadeSelecionada = gravidadeComboBox.getValue();
        String statusSelecionado = statusComboBox.getValue();

        if (bairroSelecionado == null && gravidadeSelecionada == null && statusSelecionado == null) {
            tabelaOcorrencias.setItems(ocorrencias);
            return;
        }

        var filtradas = ocorrencias.stream()
                .filter(oc -> {
                    if (oc == null) return false;

                    boolean bairroOk = (bairroSelecionado == null)
                            || (oc.getBairro() != null
                            && Objects.equals(oc.getBairro().getNome(), bairroSelecionado));

                    boolean gravidadeOk = (gravidadeSelecionada == null)
                            || (oc.getGravidadeOcorrencia() != null
                            && Objects.equals(oc.getGravidadeOcorrencia().getDescricao(), gravidadeSelecionada));

                    boolean statusOk = (statusSelecionado == null)
                            || (oc.getStatusOcorrencia() != null
                            && Objects.equals(oc.getStatusOcorrencia().getDescricao(), statusSelecionado));

                    return bairroOk && gravidadeOk && statusOk;
                })
                .toList();

        tabelaOcorrencias.setItems(FXCollections.observableArrayList(filtradas));
    }

    private void abrirDetalhes(Ocorrencia ocorrencia) {
        AppContext.getInstance().setOcorrenciaDetalhes(ocorrencia);
        navigator.showModal(Screens.DETALHES, "Detalhes");  //TODO talvez depois trocar de modal pra stage normal?
    }

    private void abrirDespachar(Ocorrencia ocorrencia) {
        if (ocorrencia.getStatusOcorrencia() != StatusOcorrencia.ABERTA) {
            AlertUtil.showInfo("Ação Inválida", "Só é possível despachar ocorrências abertas.");
            return;
        }

        AppContext.getInstance().setOcorrenciaParaDespachar(ocorrencia);

        navigator.showModal(Screens.DESPACHAR, "Despachar ambulância");

        carregarOcorrencias();
    }

    private void carregarOcorrencias() {
        Task<ObservableList<Ocorrencia>> task = new Task<>() {
            @Override
            protected ObservableList<Ocorrencia> call() {
                List<Ocorrencia> ocorrencias = service.listarTodas();
                if (ocorrencias.isEmpty()) {
                    tabelaOcorrencias.setPlaceholder(new Label("Nenhuma ocorrência encontrada"));
                }
                return FXCollections.observableArrayList(ocorrencias);
            }

            @Override
            protected void succeeded() {
                ocorrencias.setAll(getValue());
                tabelaOcorrencias.setItems(ocorrencias);
            }

            @Override
            protected void failed() {
                getException().printStackTrace();
                tabelaOcorrencias.setPlaceholder(new Label("Houve um erro ao carregar as ocorrências"));
            }
        };

        new Thread(task).start();
    }

    private void abrirEditarOcorrencia(Ocorrencia ocorrencia) {
        if (ocorrencia.getStatusOcorrencia() == StatusOcorrencia.CONCLUIDA ||
                ocorrencia.getStatusOcorrencia() == StatusOcorrencia.CANCELADA) {
            AlertUtil.showInfo("Ação Inválida", "Não é possível editar ocorrências finalizadas.");
            return;
        }
        AppContext.getInstance().setOcorrenciaEmEdicao(ocorrencia);
        navigator.showModal(Screens.CRIAR_OCORRENCIA, "Editar Ocorrência");
    }

    private void cancelarOcorrencia(Ocorrencia ocorrencia) {
        service.cancelarOcorrencia(ocorrencia);
        carregarOcorrencias();
    }

    private void deletarOcorrencia(Ocorrencia ocorrencia) {
        if (ocorrencia.getStatusOcorrencia() != StatusOcorrencia.ABERTA) {
            AlertUtil.showError("Erro",
                    "Não é possível deletar uma ocorrência com histórico. Cancele-a, em vez disso.");
            return;
        }
        var result = AlertUtil.showConfirmation("Deletar", "Deseja deletar essa ocorrência?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.deletar(ocorrencia.getId());
            tabelaOcorrencias.getItems().remove(ocorrencia);
            carregarOcorrencias();
        }
    }

    @FXML
    private void handleAtualizarLista() {
        carregarOcorrencias();
    }

    @FXML
    private void handleClearFields() {
        bairroComboBox.getSelectionModel().clearSelection();
        gravidadeComboBox.getSelectionModel().clearSelection();

        if (statusComboBox != null) statusComboBox.getSelectionModel().clearSelection();

        tabelaOcorrencias.setItems(ocorrencias);
    }

    @FXML
    private void criarOcorrencia() {
        navigator.showModal(Screens.CRIAR_OCORRENCIA, "Criar Ocorrência");
        carregarOcorrencias();
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
