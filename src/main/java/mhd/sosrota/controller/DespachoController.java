package mhd.sosrota.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.presentation.OpcaoDespacho;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.AtendimentoService;
import mhd.sosrota.util.AlertUtil;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 04/12/2025
 * @brief Class DespachoController
 */
public class DespachoController implements Navigable {
    @FXML
    private Label lblLocal;
    @FXML
    private Label lblTipo;
    @FXML
    private Label lblGravidade;
    @FXML
    private Label lblSla;

    @FXML
    private TableView<OpcaoDespacho> tabelaAmbulancias;
    @FXML
    private TableColumn<OpcaoDespacho, String> colPlaca;
    @FXML
    private TableColumn<OpcaoDespacho, String> colTipo;
    @FXML
    private TableColumn<OpcaoDespacho, String> colLocal;
    @FXML
    private TableColumn<OpcaoDespacho, String> colDistancia;
    @FXML
    private TableColumn<OpcaoDespacho, String> colTempo;

    @FXML
    private Button btnConfirmar;

    private Ocorrencia ocorrenciaAtual;
    private Navigator navigator;
    private AtendimentoService atendimentoService;

    public void initialize() {
        colPlaca.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPlaca()));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo()));
        colLocal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLocalAtual()));
        colDistancia.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDistanciaFormatada()));
        colTempo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTempoFormatado()));

        this.ocorrenciaAtual = AppContext.getInstance().getOcorrenciaParaDespachar();
        AppContext.getInstance().setOcorrenciaParaDespachar(null);

        if (this.ocorrenciaAtual == null) {
            System.err.println("Erro: Nenhuma ocorrência selecionada para despacho.");
            return; // Idealmente fecharia a janela ou mostraria erro
        }
        preencherResumo();

        // 4. Carregar Lista (Aqui entra o algoritmo!)
        carregarSugestoesDeAmbulancia();

        // 5. Bloquear botão se nada selecionado
        btnConfirmar.disableProperty().bind(tabelaAmbulancias.getSelectionModel().selectedItemProperty().isNull());
    }

    private void preencherResumo() {
        lblLocal.setText(ocorrenciaAtual.getBairro().getNome());
        lblTipo.setText(ocorrenciaAtual.getTipoOcorrencia());
        lblGravidade.setText(ocorrenciaAtual.getGravidadeOcorrencia().getDescricao());

        // Simulação simples do SLA restante (poderia usar a lógica do timer se quisesse)
        // Apenas para dar noção estática ao abrir a tela
        LocalDateTime limite = ocorrenciaAtual.getLimiteSLA().toLocalDateTime();
        if (limite != null) {
            long minutosRestantes = java.time.Duration.between(LocalDateTime.now(), limite).toMinutes();
            lblSla.setText(minutosRestantes + " min");
        }
    }

    private void carregarSugestoesDeAmbulancia() {
        this.atendimentoService = AppContext.getInstance().getDespachoService();

        Task<List<OpcaoDespacho>> task = new Task<>() {
            @Override
            protected List<OpcaoDespacho> call() {
                return atendimentoService.buscarOpcoesDeDespacho(ocorrenciaAtual);
            }

            @Override
            protected void succeeded() {
                var opcoes = getValue();
                if (opcoes.isEmpty()) {
                    tabelaAmbulancias.setPlaceholder(new Label("Nenhuma ambulância disponível ou rota não encontrada."));
                } else {
                    tabelaAmbulancias.setItems(FXCollections.observableArrayList(opcoes));
                }
            }

            @Override
            protected void failed() {
                getException().printStackTrace();
                tabelaAmbulancias.setPlaceholder(new Label("Nenhuma ambulância disponível ou rota não encontrada."));
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleConfirmarDespacho() {
        OpcaoDespacho selecao = tabelaAmbulancias.getSelectionModel().getSelectedItem();
        if (selecao == null) return;

        Ocorrencia ocorrencia = this.ocorrenciaAtual;
        double distancia = selecao.getDistanciaKm();
        double tempo = selecao.getTempoEstimadoMin();

        UiUtils.setButtonLoading(btnConfirmar, true, "Confirmar Despacho");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                atendimentoService.realizarDespacho(ocorrencia, selecao.getAmbulancia(), distancia, tempo);
                return null;
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(btnConfirmar, false, "Confirmar Despacho");
                AlertUtil.showInfo( "Sucesso", "Ambulância despachada com sucesso.");
                handleCancelar();
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(btnConfirmar, false, "Confirmar Despacho");
                getException().printStackTrace();
                AlertUtil.showError("Erro", "Algo deu errado ao despachar a ambulância");
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleCancelar() {
        navigator.closeStage(btnConfirmar);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
