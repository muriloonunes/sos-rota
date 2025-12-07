package mhd.sosrota.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.util.Pair;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.GrafoCidade;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.Rua;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.StatusOcorrencia;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.service.GrafoCidadeService;
import mhd.sosrota.service.OcorrenciaService;
import mhd.sosrota.util.AlertUtil;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 22/11/2025
 * @brief Class DashboardController
 */
public class DashboardController implements Navigable {
    @FXML
    private HBox ocorrenciasAbertasHbox, ambulanciasDisponiveisHbox, ambulanciasAtendimentoHbox;
    @FXML
    private Label ocorrenciasAbertasQtd, ambulanciasDisponiveisQtd, ambulanciasAtendimentoQtd;
    @FXML
    private TableView<Ocorrencia> ocorrenciasTableView;
    @FXML
    private TableColumn<Ocorrencia, String> bairroColumn, gravidadeColumn, statusColumn;
    @FXML
    private TableColumn<Ocorrencia, OffsetDateTime> aberturaColumn, slaColumn;
    @FXML
    private TableColumn<Ocorrencia, Void> acoesColumn;
    @FXML
    private Pane mapaPane;

    private Navigator navigator;

    private OcorrenciaService ocorrenciaService;
    private AmbulanciaService ambulanciaService;
    private GrafoCidadeService grafoService;
    private GrafoCidade grafo;

    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    @FXML
    public void initialize() {
        ocorrenciasAbertasQtd.setText("0");
        ambulanciasDisponiveisQtd.setText("0");
        ambulanciasAtendimentoQtd.setText("0");

        this.grafoService = AppContext.getInstance().getGrafoService();
        this.ambulanciaService = AppContext.getInstance().getAmbulanciaService();
        this.ocorrenciaService = AppContext.getInstance().getOcorrenciaService();
        Platform.runLater(this::carregarDados);

        SVGImage exclamationIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/exclamacao.svg"))).scaleTo(48);
        SVGImage ambulanciasIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/ambulancias.svg"))).scaleTo(48);
        SVGImage pulsoIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/pulso.svg"))).scaleTo(48);
        ocorrenciasAbertasHbox.getChildren().add(exclamationIcon);
        ambulanciasDisponiveisHbox.getChildren().add(ambulanciasIcon);
        ambulanciasAtendimentoHbox.getChildren().add(pulsoIcon);
    }

    private void configurarTabela() {
        bairroColumn.setCellValueFactory(new PropertyValueFactory<>("bairro"));

        gravidadeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getGravidadeOcorrencia().getDescricao()
        ));

        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStatusOcorrencia().getDescricao()
        ));

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

            private final Button cancelarButton = new Button();
            private final Button despacharButton = new Button();

            {
                despacharButton.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/rota.svg"))).scaleTo(12));
                cancelarButton.setGraphic(SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/cancelar.svg"))).scaleTo(12));

                despacharButton.getStyleClass().add("btn-primary");
                cancelarButton.getStyleClass().add("btn-ocorrencia");

                despacharButton.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    abrirDespachar(ocorrencia);
                });

                cancelarButton.setOnAction(_ -> {
                    Ocorrencia ocorrencia = getTableRow().getItem();
                    cancelarOcorrencia(ocorrencia);
                });

                acoesBox.setAlignment(Pos.CENTER);
                acoesBox.getChildren().addAll(despacharButton, cancelarButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getIndex() < 0 || getTableView().getItems().get(getIndex()) == null) {
                    setGraphic(null);
                } else {
                    Ocorrencia oc = getTableView().getItems().get(getIndex());

                    boolean isAberta = oc.getStatusOcorrencia() == StatusOcorrencia.ABERTA;

                    despacharButton.setDisable(!isAberta);

                    cancelarButton.setDisable(oc.getStatusOcorrencia() == StatusOcorrencia.CONCLUIDA
                            || oc.getStatusOcorrencia() == StatusOcorrencia.CANCELADA);

                    setGraphic(acoesBox);
                }
            }
        });
    }

    private void carregarDados() {
        carregarGrafo();
        carregarAmbulancias();
        configurarTabela();
        carregarOcorrencias();
        Timeline relogio = new Timeline(new KeyFrame(
                javafx.util.Duration.seconds(1), _ -> ocorrenciasTableView.refresh())
        );
        relogio.setCycleCount(Timeline.INDEFINITE);
        relogio.play();
    }

    private void carregarGrafo() {
        ProgressIndicator pi = new ProgressIndicator();
        pi.setPrefSize(100, 100);
        mapaPane.getChildren().add(pi);

        pi.setLayoutX(mapaPane.getWidth() / 2 - 25);
        pi.setLayoutY(mapaPane.getHeight() / 2 - 25);

        Task<GrafoCidade> task = new Task<>() {
            @Override
            protected GrafoCidade call() {
                return grafoService.obterGrafo();
            }

            @Override
            protected void succeeded() {
                mapaPane.getChildren().remove(pi);
                grafo = getValue();
                desenharMapa();
            }

            @Override
            protected void failed() {
                mapaPane.getChildren().remove(pi);
                getException().printStackTrace();
            }
        };
        new Thread(task).start();
    }

    private void carregarAmbulancias() {
        Task<Pair<Long, Long>> task = new Task<>() {
            @Override
            protected Pair<Long, Long> call() {
                long disponiveis = ambulanciaService.obterQtdAmbulanciaStatus(StatusAmbulancia.DISPONIVEL);
                long atendimento = ambulanciaService.obterQtdAmbulanciaStatus(StatusAmbulancia.EM_ATENDIMENTO);
                return new Pair<>(disponiveis, atendimento);
            }

            @Override
            protected void succeeded() {
                Pair<Long, Long> resultado = getValue();
                ambulanciasDisponiveisQtd.setText(String.valueOf(resultado.getKey()));
                ambulanciasAtendimentoQtd.setText(String.valueOf(resultado.getValue()));
            }

            @Override
            protected void failed() {
                ambulanciasDisponiveisQtd.setText("0");
                ambulanciasAtendimentoQtd.setText("0");

                getException().printStackTrace();
            }
        };

        new Thread(task).start();
    }

    private void carregarOcorrencias() {
        Task<Pair<ObservableList<Ocorrencia>, Integer>> task = new Task<>() {
            @Override
            protected Pair<ObservableList<Ocorrencia>, Integer> call() {
                var lista = ocorrenciaService.listarTodas().stream().limit(10).toList();
                var quantidade = ocorrenciaService.obterQuantidadeOcorrenciasPorStatus(StatusOcorrencia.ABERTA);
                var ocorrenciasObservable = FXCollections.observableArrayList(lista);
                return new Pair<>(ocorrenciasObservable, quantidade);
            }

            @Override
            protected void succeeded() {
                var resultado = getValue();
                ocorrenciasTableView.setItems(resultado.getKey());
                ocorrenciasAbertasQtd.setText(String.valueOf(resultado.getValue()));
            }

            @Override
            protected void failed() {
                ocorrenciasAbertasQtd.setText("0");
                ocorrenciasTableView.setPlaceholder(new Label("Houve um erro ao carregar as ocorrências"));

                getException().printStackTrace();
            }
        };

        new Thread(task).start();
    }

    private void abrirDespachar(Ocorrencia ocorrencia) {
        if (ocorrencia.getStatusOcorrencia() != StatusOcorrencia.ABERTA) {
            AlertUtil.showInfo("Ação Inválida", "Só é possível despachar ocorrências abertas.");
            return;
        }

        AppContext.getInstance().setOcorrenciaParaDespachar(ocorrencia);

        navigator.showModal(Screens.DESPACHAR, "Despachar ambulância");

        carregarOcorrencias();
        carregarAmbulancias();
    }

    private void cancelarOcorrencia(Ocorrencia ocorrencia) {
        ocorrenciaService.cancelarOcorrencia(ocorrencia);
        carregarOcorrencias();
        carregarAmbulancias();
    }

    @FXML
    private void handleAtualizarLista() {
        carregarOcorrencias();
        carregarAmbulancias();
    }

    private void desenharMapa() {
        mapaPane.getChildren().clear();
        mapaPane.setMaxHeight(500);

        Group mundoGroup = new Group();

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Bairro b : grafo.getBairros()) {
            if (b.getX() < minX) minX = b.getX();
            if (b.getY() < minY) minY = b.getY();
            if (b.getX() > maxX) maxX = b.getX();
            if (b.getY() > maxY) maxY = b.getY();
        }

        double padding = 50.0;

        double offsetX = -minX + padding;
        double offsetY = -minY + padding;

        // (Opcional) Fator de escala se o mapa for maior que a tela
        // Aqui assumimos 1:1, mas você poderia calcular um ratio baseado no mapContainer.getWidth()

        for (Rua rua : grafo.getRuas()) {
            Bairro origem = rua.getOrigem();
            Bairro destino = rua.getDestino();

            Line linha = new Line(
                    origem.getX() + offsetX,
                    origem.getY() + offsetY,
                    destino.getX() + offsetX,
                    destino.getY() + offsetY
            );
            linha.setUserData(rua);

            mundoGroup.getChildren().add(linha);
        }

        for (Bairro b : grafo.getBairros()) {
            double finalX = b.getX() + offsetX;
            double finalY = b.getY() + offsetY;

            Circle circulo = new Circle(finalX, finalY, 15);
            circulo.getStyleClass().add("bairro");

            if (b.temBase()) {
                circulo.getStyleClass().add("bairro-base");
            }

            circulo.setUserData(b);

            Text texto = new Text(finalX + 17, finalY + 4, b.getNome());

            mundoGroup.getChildren().addAll(circulo, texto);

            configurarHoverBairro(circulo, mundoGroup);
        }
        double zoom = 0.9;
        mundoGroup.setScaleX(zoom);
        mundoGroup.setScaleY(zoom);

        mundoGroup.getTransforms().add(new Translate(0, 0));
        mundoGroup.setTranslateX(mundoGroup.getTranslateX() + 50);
        mundoGroup.setTranslateY(mundoGroup.getTranslateY() - 350);

        mapaPane.getChildren().add(mundoGroup);
        configurarNavegacao(mundoGroup);
        configurarRecorte();
    }

    private void configurarHoverBairro(Circle circulo, Group mundoGroup) {
        circulo.setOnMouseEntered(_ -> {
            Bairro b = (Bairro) circulo.getUserData();

            mundoGroup.getChildren().forEach(node -> node.getStyleClass().add("escurecido"));

            circulo.getStyleClass().remove("escurecido");
            circulo.getStyleClass().add("bairro-destacado");

            for (Node n : mundoGroup.getChildren()) {
                if (n instanceof Line line) {
                    Rua rua = (Rua) line.getUserData();
                    if (rua != null &&
                            (rua.getOrigem().equals(b) || rua.getDestino().equals(b))) {

                        line.getStyleClass().remove("escurecido");
                        line.getStyleClass().add("rua-destacada");
                    }
                }
            }

            for (Node n : mundoGroup.getChildren()) {
                if (n instanceof Text texto) {
                    if (texto.getText().equals(b.getNome())) {
                        texto.getStyleClass().remove("escurecido");
                        texto.getStyleClass().add("bairro-label-destacado");
                    }
                }
            }
        });

        circulo.setOnMouseExited(_ -> mundoGroup.getChildren().forEach(node -> node.getStyleClass().removeAll(
                "escurecido",
                "bairro-destacado",
                "bairro-label-destacado",
                "rua-destacada"
        )));
    }

    private void configurarNavegacao(Group mundoGroup) {
        mapaPane.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();

            translateAnchorX = mundoGroup.getTranslateX();
            translateAnchorY = mundoGroup.getTranslateY();

            mapaPane.setCursor(Cursor.CLOSED_HAND);
        });

        mapaPane.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseAnchorX;
            double deltaY = event.getSceneY() - mouseAnchorY;

            double novoX = translateAnchorX + deltaX;
            double novoY = translateAnchorY + deltaY;

            double scale = mundoGroup.getScaleX();
            double graphWidth = mundoGroup.getBoundsInLocal().getWidth() * scale;
            double graphHeight = mundoGroup.getBoundsInLocal().getHeight() * scale;

            double viewWidth = mapaPane.getWidth();
            double viewHeight = mapaPane.getHeight();
            double margin = 300.0;
            double minX = viewWidth - graphWidth - margin;
            double maxX = margin;

            double minY = viewHeight - graphHeight - margin;
            double maxY = margin;

            if (graphWidth < viewWidth) {
                minX = 0;
                maxX = viewWidth - graphWidth;
            }
            if (graphHeight < viewHeight) {
                minY = 0;
                maxY = viewHeight - graphHeight;
            }

            novoX = Math.max(minX, Math.min(novoX, maxX));
            novoY = Math.max(minY, Math.min(novoY, maxY));

            mundoGroup.setTranslateX(novoX);
            mundoGroup.setTranslateY(novoY);
        });


        mapaPane.setOnMouseReleased(_ -> mapaPane.setCursor(Cursor.OPEN_HAND));

        mapaPane.setCursor(Cursor.OPEN_HAND);
    }

    private void configurarRecorte() {
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(mapaPane.widthProperty());
        clip.heightProperty().bind(mapaPane.heightProperty());

        mapaPane.setClip(clip);
    }

    @FXML
    private void criarOcorrencia() {
        navigator.showModal(Screens.CRIAR_OCORRENCIA, "Criar Ocorrência");
        carregarOcorrencias();
        carregarAmbulancias();
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
