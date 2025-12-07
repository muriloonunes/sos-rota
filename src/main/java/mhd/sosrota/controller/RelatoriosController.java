package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mhd.sosrota.model.enums.GravidadeOcorrencia;

public class RelatoriosController {

    @FXML private TextArea txtTempoMedio;
    @FXML private TextArea txtMapaBairros;


    @FXML
    private void handleTempoMedioResposta() {
        txtTempoMedio.setText("Tempo médio de resposta: 12 minutos"); // placeholder
    }

    @FXML
    private void handleMapaBairros() {
        txtMapaBairros.setText("Bairro A: 5 ocorrências\nBairro B: 3 ocorrências"); // placeholder
    }
}
