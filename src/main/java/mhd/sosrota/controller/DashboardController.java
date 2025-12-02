package mhd.sosrota.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.GrafoCidade;
import mhd.sosrota.model.Rua;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.service.GrafoCidadeService;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

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
    private Label ambulanciasDisponiveisQtd, ambulanciasAtendimentoQtd;
    @FXML
    private TableView<String> ocorrenciasTableView;
    @FXML
    private TableColumn<String, String> idColumn, localColumn, gravidadeColumn, tipoColumn, statusColumn, aberturaColumn, acoesColumn;
    @FXML
    private Pane mapaPane;

    private Navigator navigator;

    private AmbulanciaService ambulanciaService;
    private GrafoCidadeService grafoService;
    private GrafoCidade grafo;

    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    @FXML
    public void initialize() {
        ambulanciasDisponiveisQtd.setText("0");
        ambulanciasAtendimentoQtd.setText("0");

        this.grafoService = AppContext.getInstance().getGrafoService();
        this.ambulanciaService = AppContext.getInstance().getAmbulanciaService();
        Platform.runLater(this::carregarDados);

        SVGImage exclamationIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/exclamacao.svg"))).scaleTo(48);
        SVGImage ambulanciasIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/ambulancias.svg"))).scaleTo(48);
        SVGImage pulsoIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/pulso.svg"))).scaleTo(48);
        ocorrenciasAbertasHbox.getChildren().add(exclamationIcon);
        ambulanciasDisponiveisHbox.getChildren().add(ambulanciasIcon);
        ambulanciasAtendimentoHbox.getChildren().add(pulsoIcon);
    }

    private void carregarDados() {
        carregarGrafo();
        carregarAmbulanciasDisponiveis();
        carregarAmbulanciasAtendimento();
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

    private void carregarAmbulanciasDisponiveis() {
        Task<Long> task = new Task<>() {
            @Override
            protected Long call() {
                return ambulanciaService.obterAmbulanciaStatus(StatusAmbulancia.DISPONIVEL);
            }

            @Override
            protected void succeeded() {
                long resultado = getValue();
                ambulanciasDisponiveisQtd.setText(String.valueOf(resultado));
            }

            @Override
            protected void failed() {
                ambulanciasDisponiveisQtd.setText("0");
            }
        };

        new Thread(task).start();
    }
    private void carregarAmbulanciasAtendimento() {
        Task<Long> task = new Task<>() {
            @Override
            protected Long call() {
                return ambulanciaService.obterAmbulanciaStatus(StatusAmbulancia.EM_ATENDIMENTO);
            }

            @Override
            protected void succeeded() {
                long resultado = getValue();
                ambulanciasAtendimentoQtd.setText(String.valueOf(resultado));
            }

            @Override
            protected void failed() {
                ambulanciasAtendimentoQtd.setText("0");

                getException().printStackTrace();
            }
        };

        new Thread(task).start();
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

        mundoGroup.getTransforms().add(new Translate(0,0));
        mundoGroup.setTranslateX(mundoGroup.getTranslateX() + 50);
        mundoGroup.setTranslateY(mundoGroup.getTranslateY() - 350);

        mapaPane.getChildren().add(mundoGroup);
        configurarNavegacao(mundoGroup);
        configurarRecorte();
    }

    private void configurarHoverBairro(Circle circulo, Group mundoGroup) {
        circulo.setOnMouseEntered(_ -> {
            Bairro b = (Bairro) circulo.getUserData();

            mundoGroup.getChildren().forEach(node -> {
                node.getStyleClass().add("escurecido");
            });

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

        circulo.setOnMouseExited(e -> {
            mundoGroup.getChildren().forEach(node -> {
                node.getStyleClass().removeAll(
                        "escurecido",
                        "bairro-destacado",
                        "bairro-label-destacado",
                        "rua-destacada"
                );
            });
        });
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
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
