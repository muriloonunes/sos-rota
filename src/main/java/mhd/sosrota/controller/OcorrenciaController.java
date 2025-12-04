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
import javafx.scene.paint.Color;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.service.OcorrenciaService;
import mhd.sosrota.util.AlertUtil;
import org.girod.javafx.svgimage.SVGLoader;

import java.time.Duration;
import java.time.LocalDateTime;
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
        slaColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(OffsetDateTime item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    OffsetDateTime limite = ocorrencia.getLimiteSLA();

                    if (limite == null) {
                        setText("Aguardando...");
                        return;
                    }
                    Duration duration = Duration.between(LocalDateTime.now(), limite);
                    boolean estourado = duration.isNegative();
                    long segundosAbs = Math.abs(duration.getSeconds());
                    long minutos = segundosAbs / 60;
                    long segundos = segundosAbs % 60;

                    String textoTempo = String.format("%s%02d:%02d",
                            estourado ? "-" : "",
                            minutos,
                            segundos
                    );

                    setText(textoTempo);

                    if (estourado) {
                        setTextFill(Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    } else if (minutos < 2) {
                        setTextFill(Color.ORANGE);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });

        acoesColumn.setCellFactory(_ -> new TableCell<>() {
            private final HBox acoesBox = new HBox(10);
            private final MenuButton btnMenu = new MenuButton();
            private final MenuItem itemDespachar = new MenuItem("Despachar");
            private final MenuItem itemEditar = new MenuItem("Editar");
            private final MenuItem itemCancelar = new MenuItem("Cancelar");
            private final Button deletarButton = new Button();

            {
                deletarButton.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/deletar.svg"))).scaleTo(12));
                btnMenu.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/acoes.svg"))).scaleTo(12));
                btnMenu.getStyleClass().add("btn-acoes");
                //TODO estilizar o botao de acoes
                deletarButton.getStyleClass().add("btn-ocorrencia");

                deletarButton.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    deletarOcorrencia(ocorrencia);
                });

                btnMenu.getItems().addAll(itemDespachar, itemEditar, new SeparatorMenuItem(), itemCancelar);

                acoesBox.setAlignment(Pos.CENTER);
                acoesBox.getChildren().addAll(btnMenu, deletarButton);

                itemDespachar.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableView().getItems().get(getIndex());
                    abrirDespachar(ocorrencia);
                });

                itemEditar.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableView().getItems().get(getIndex());
                    abrirEditarOcorrencia(ocorrencia);
                });

                itemCancelar.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableView().getItems().get(getIndex());
                    cancelarOcorrencia(ocorrencia);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                } else {
                    Ocorrencia oc = getTableView().getItems().get(getIndex());

                    boolean isAberta = oc.getStatusOcorrencia() == StatusOcorrencia.ABERTA;

                    itemDespachar.setDisable(!isAberta);

                    itemEditar.setDisable(oc.getStatusOcorrencia() == StatusOcorrencia.CONCLUIDA
                            || oc.getStatusOcorrencia() == StatusOcorrencia.CANCELADA);

                    itemCancelar.setDisable(oc.getStatusOcorrencia() == StatusOcorrencia.CONCLUIDA
                            || oc.getStatusOcorrencia() == StatusOcorrencia.CANCELADA);

                    setGraphic(acoesBox);
                }
            }
        });
    }

    private void abrirDespachar(Ocorrencia ocorrencia) {
        if (ocorrencia.getStatusOcorrencia() != StatusOcorrencia.ABERTA) {
            AlertUtil.showInfo("Ação Inválida", "Só é possível despachar ocorrências abertas.");
            return;
        }

        //TODO
    }

    private void carregarOcorrencias() {
        Task<ObservableList<Ocorrencia>> task = new Task<>() {
            @Override
            protected ObservableList<Ocorrencia> call() {
                List<Ocorrencia> ocorrencias = service.listarTodas();
                if (ocorrencias.isEmpty() || ocorrencias == null) {
                    tabelaOcorrencias.setPlaceholder(new Label("Nenhuma ocorrencia encontrada"));
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
        if (ocorrencia.getStatusOcorrencia() != StatusOcorrencia.ABERTA &&
                ocorrencia.getStatusOcorrencia() != StatusOcorrencia.DESPACHADA) {
            AlertUtil.showInfo("Ação Inválida", "Esta ocorrência já está finalizada.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar Ocorrência");
        dialog.setHeaderText("Motivo do Cancelamento");
        dialog.setContentText("Justificativa:");
        dialog.showAndWait().ifPresent(justificativa -> {
            if (justificativa.trim().isEmpty()) {
                AlertUtil.showInfo("Erro", "A justificativa é obrigatória.");
                return;
            }

            try {
                ocorrencia.setStatusOcorrencia(StatusOcorrencia.CANCELADA);
                String novaObs = (ocorrencia.getObservacao() != null ? ocorrencia.getObservacao() : "")
                        + "\n[CANCELAMENTO]: " + justificativa;
                ocorrencia.setObservacao(novaObs);

                service.salvar(ocorrencia);

                tabelaOcorrencias.refresh();

            } catch (Exception e) {
                AlertUtil.showError("Erro", "Falha ao cancelar: " + e.getMessage());
            }
        });
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
        }
    }

    @FXML
    private void handleClearFields() {
        bairroComboBox.getSelectionModel().clearSelection();
        gravidadeComboBox.getSelectionModel().clearSelection();

        if (statusComboBox != null)
            statusComboBox.getSelectionModel().clearSelection();
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
