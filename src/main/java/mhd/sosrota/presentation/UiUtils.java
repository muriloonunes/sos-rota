package mhd.sosrota.presentation;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;
import mhd.sosrota.util.AlertUtil;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 29/11/2025
 * @brief Class AmbulanciaSetup
 */
public class UiUtils {
    private static final URL URL_EDITAR = UiUtils.class.getResource("/images/editar.svg");
    private static final URL URL_DELETAR = UiUtils.class.getResource("/images/deletar.svg");

    public static void configurarCamposAmbulancia(TextField placaField, ComboBox<String> tipoComboBox, ComboBox<String> statusComboBox, ComboBox<Bairro> baseComboBox) {
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

        placaField.textProperty().addListener((_, _, newValue) -> {
            if (placaField.getText().length() > 7) {
                placaField.setText(newValue.substring(0, 7));
            }
        });
    }

    public static void setButtonLoading(Button button, boolean isLoading, String originalText) {
        if (isLoading) {
            ProgressIndicator pi = new ProgressIndicator();
            pi.setPrefSize(16, 16);

            button.setGraphic(pi);
            button.setText(null);
            button.setMouseTransparent(true);
            button.setFocusTraversable(true);
        } else {
            button.setGraphic(null);
            button.setText(originalText);
            button.setMouseTransparent(false);
            button.setFocusTraversable(false);
        }
    }

    /**
     * Cria uma fábrica de células genérica com botões de editar e deletar.
     *
     * @param <T>             O tipo do objeto da tabela (ex: Ambulancia, Paciente)
     * @param onEditar        Ação ao clicar em editar (Consumer recebe o objeto da linha)
     * @param onDeletar       Ação ao confirmar a deleção (Consumer recebe o objeto da linha)
     * @param tituloAlerta    Título da janela de confirmação
     * @param descricaoAlerta Mensagem da janela de confirmação
     */
    public static <T> Callback<TableColumn<T, Void>, TableCell<T, Void>> criarColunaAcoes(
            Consumer<T> onEditar,
            Consumer<T> onDeletar,
            String tituloAlerta,
            String descricaoAlerta
    ) {
        return _ -> new TableCell<>() {
            private final HBox acoesBox = new HBox(10);
            private final Button editarButton = new Button();
            private final Button deletarButton = new Button();

            final SVGImage editarImage = SVGLoader.load(Objects.requireNonNull(URL_EDITAR)).scaleTo(12);
            final SVGImage deleteImage = SVGLoader.load(Objects.requireNonNull(URL_DELETAR)).scaleTo(12);

            {
                editarButton.setGraphic(editarImage);
                deletarButton.setGraphic(deleteImage);
                editarButton.getStyleClass().add("btn-primary");
                deletarButton.getStyleClass().add("btn-ocorrencia");
                acoesBox.setAlignment(Pos.CENTER);
                acoesBox.getChildren().addAll(editarButton, deletarButton);

                editarButton.setOnAction(evt -> {
                    T item = getTableView().getItems().get(getIndex());
                    if (onEditar != null) onEditar.accept(item);
                });

                deletarButton.setOnAction(evt -> {
                    var result = AlertUtil.showConfirmation(tituloAlerta, descricaoAlerta);
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        T item = getTableView().getItems().get(getIndex());
                        if (onDeletar != null) onDeletar.accept(item);
                    }
                });
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
        };
    }

    public static <T> Callback<ListView<T>, ListCell<T>> comboCellFactory(Function<T, String> extractor) {
        return _ -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : extractor.apply(item));
            }
        };
    }
}
