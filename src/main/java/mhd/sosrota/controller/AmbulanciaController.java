package mhd.sosrota.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class CadastrarAmbulanciaController
 */
public class AmbulanciaController implements Navigable {
    @FXML
    private ComboBox<String> tipoComboBox;
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

    private final int itensPorPagina = 8;
    private Navigator navigator;

    @FXML
    private void initialize() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        configurarTabela();

        carregarAmbulancias();

        UiUtils.configurarCamposAmbulancia(placaTextField, tipoComboBox, baseComboBox);
        tabelaAmbulancias.setItems(ambulancias);

        cadastrarAmbulanciaButton.disableProperty().bind(
                (placaTextField.textProperty().isEmpty()
                        .or(tipoComboBox.valueProperty().isNull())
                        .or(baseComboBox.valueProperty().isNull()))
                        .or(placaTextField.textProperty().length().lessThan(7))
        );

        paginacaoAmbulancias.currentPageIndexProperty().addListener((_, _, _) ->
                UiUtils.atualizarPaginacao(
                        paginacaoAmbulancias,
                        tabelaAmbulancias,
                        ambulancias,
                        itensPorPagina,
                        () -> tabelaAmbulancias.refresh()
                )
        );
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

        colunaAcoes.setCellFactory(UiUtils.criarColunaAcoes(
                (ambulancia) -> {
                    abrirEditarAmbulancias(ambulancia);
                    carregarAmbulancias();
                },
                (ambulancia) -> {
                    service.deletarAmbulancia(ambulancia.getId());
                    carregarAmbulancias();
                },
                "Deletar ambulância",
                "Tem certeza que deseja deletar a ambulância?"
        ));
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
                UiUtils.atualizarPaginacao(paginacaoAmbulancias, tabelaAmbulancias, ambulancias, itensPorPagina);
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
        baseComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRegistrarAmbulancia() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        String placa = placaTextField.getText().toUpperCase();
        String tipoDesc = tipoComboBox.getValue();
        Bairro base = baseComboBox.getValue();

        UiUtils.setButtonLoading(cadastrarAmbulanciaButton, true, "Registrar Ambulância");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.cadastrarAmbulancia(placa, tipoDesc, base);
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

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
