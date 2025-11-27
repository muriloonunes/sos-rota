package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;
import mhd.sosrota.service.GrafoBairroService;
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
    private TableView<String> ocorrenciasTableView;
    @FXML
    private TableColumn<String, String> idColumn, localColumn, gravidadeColumn, tipoColumn, statusColumn, aberturaColumn, acoesColumn;

    private Navigator navigator;

    private final GrafoBairroService grafoService = AppContext.getInstance().getGrafoService();

    @FXML
    public void initialize() {
        SVGImage exclamationIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/exclamacao.svg"))).scaleTo(48);
        SVGImage ambulanciasIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/ambulancias.svg"))).scaleTo(48);
        SVGImage pulsoIcon = SVGLoader.load(Objects.requireNonNull(getClass().getResource("/images/pulso.svg"))).scaleTo(48);
        ocorrenciasAbertasHbox.getChildren().add(exclamationIcon);
        ambulanciasDisponiveisHbox.getChildren().add(ambulanciasIcon);
        ambulanciasAtendimentoHbox.getChildren().add(pulsoIcon);

//        GrafoBairro grafo = grafoBairroService.carregarGrafo();
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
