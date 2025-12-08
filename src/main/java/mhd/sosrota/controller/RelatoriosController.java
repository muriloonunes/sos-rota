package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.service.RelatorioService;

import java.util.Map;

public class RelatoriosController {
    @FXML
    private TextArea txtTempoMedio;
    @FXML
    private TextArea txtMapaBairros;

    private final RelatorioService relatorioService = new RelatorioService();

    @FXML
    public void initialize() {
        handleMapaBairros();
        handleTempoMedioResposta();
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
