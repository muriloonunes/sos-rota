package mhd.sosrota.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.service.RelatorioService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class RelatoriosController {

    @FXML private ComboBox<Long> cmbAmbulanciaId;
    @FXML private ComboBox<GravidadeOcorrencia> cmbGravidade;
    @FXML private DatePicker dtInicio;
    @FXML private DatePicker dtFim;

    @FXML private TableView<Atendimento> tabelaHistorico;

    @FXML private TextArea txtTempoMedio;
    @FXML private TextArea txtMapaBairros;

    private final RelatorioService relatorioService = new RelatorioService();

    @FXML
    public void initialize() {

        cmbGravidade.setItems(FXCollections.observableArrayList(GravidadeOcorrencia.values()));

        cmbAmbulanciaId.setItems(FXCollections.observableArrayList(1L, 2L, 3L));
    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        cmbAmbulanciaId.setValue(null);
        cmbGravidade.setValue(null);
        dtInicio.setValue(null);
        dtFim.setValue(null);
        tabelaHistorico.getItems().clear();
        txtTempoMedio.clear();
        txtMapaBairros.clear();
    }

    @FXML
    private void handleApplyFilters(ActionEvent event) {
        handleConsultarHistorico();
    }

    @FXML
    private void handleExportToCSV(ActionEvent event) {
        // se quiser implementar depois
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Exportação ainda não implementada.");
        a.show();
    }

    @FXML
    private void handleConsultarHistorico() {

        Long ambId = cmbAmbulanciaId.getValue();
        GravidadeOcorrencia grav = cmbGravidade.getValue();

        LocalDateTime inicio = dtInicio.getValue() != null ?
                dtInicio.getValue().atStartOfDay() : null;

        LocalDateTime fim = dtFim.getValue() != null ?
                dtFim.getValue().atTime(LocalTime.MAX) : null;

        var resultados = relatorioService.consultarHistorico(ambId, grav, inicio, fim);

        tabelaHistorico.setItems(FXCollections.observableArrayList(resultados));
    }

    @FXML
    private void handleTempoMedioResposta() {

        Map<GravidadeOcorrencia, Double> mapa = relatorioService.tempoMedioResposta();

        StringBuilder sb = new StringBuilder();
        sb.append("TEMPO MÉDIO DE RESPOSTA (minutos):\n\n");

        mapa.forEach((gravidade, minutos) ->
                sb.append(gravidade)
                        .append(" → ")
                        .append(String.format("%.2f min", minutos))
                        .append("\n")
        );

        txtTempoMedio.setText(sb.toString());
    }

    @FXML
    private void handleMapaBairros() {

        Map<String, Long> mapa = relatorioService.ocorrenciasPorBairro();

        StringBuilder sb = new StringBuilder();
        sb.append("OCORRÊNCIAS POR BAIRRO:\n\n");

        mapa.forEach((bairro, qtd) ->
                sb.append(bairro)
                        .append(" → ")
                        .append(qtd)
                        .append(" ocorrências\n")
        );

        txtMapaBairros.setText(sb.toString());
    }
}
