package mhd.sosrota.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.service.AtendimentoService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 07/12/2025
 * @brief Class DetalhesAtendimentoController
 */
public class DetalhesAtendimentoController implements Navigable {
    @FXML
    private Label idOcorrenciaLabel, tipoLabel, gravidadeLabel, bairroLabel, statusLabel, obsLabel;
    @FXML
    private Label ambulanciaLabel, distanciaLabel;
    @FXML
    private Label horaAberturaLabel, horaDespachoLabel, horaChegadaLabel, horaConclusaoLabel;
    @FXML
    private VBox atendimentoVbox;
    @FXML
    private Label avisoSemDespachoLabel;

    private Navigator navigator;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void initialize() {
        Ocorrencia ocorrencia = AppContext.getInstance().getOcorrenciaDetalhes();
        if (ocorrencia == null) {
            navigator.closeStage(atendimentoVbox);
        }

        idOcorrenciaLabel.setText("#" + ocorrencia.getId());
        tipoLabel.setText(ocorrencia.getTipoOcorrencia());
        gravidadeLabel.setText(ocorrencia.getGravidadeOcorrencia().getDescricao());
        bairroLabel.setText(ocorrencia.getBairro().getNome());
        statusLabel.setText(ocorrencia.getStatusOcorrencia().getDescricao());
        obsLabel.setText(ocorrencia.getObservacao() != null ? ocorrencia.getObservacao() : "-");

        if (ocorrencia.getDataHoraAbertura() != null) {
            horaAberturaLabel.setText(dtf.format(ocorrencia.getDataHoraAbertura()));
        }

        AtendimentoService service = AppContext.getInstance().getDespachoService();
        Atendimento atendimento = service.obterHistorico(ocorrencia.getId());

        if (atendimento != null) {
            atendimentoVbox.setVisible(true);
            atendimentoVbox.setManaged(true);
            avisoSemDespachoLabel.setVisible(false);

            String infoAmb = String.format("%s (%s)",
                    atendimento.getAmbulancia().getPlaca(),
                    atendimento.getAmbulancia().getTipoAmbulancia());
            ambulanciaLabel.setText(infoAmb);

            distanciaLabel.setText(String.format("%.2f Km", atendimento.getDistanciaKm()));

            horaDespachoLabel.setText(formatarData(atendimento.getDataHoraDespacho()));
            horaChegadaLabel.setText(formatarData(atendimento.getDataHoraChegada()));
            horaConclusaoLabel.setText(formatarData(atendimento.getDataHoraConclusao()));

        } else {
            atendimentoVbox.setVisible(false);
            atendimentoVbox.setManaged(false);
            avisoSemDespachoLabel.setVisible(true);

            horaDespachoLabel.setText("-");
            horaChegadaLabel.setText("-");
            horaConclusaoLabel.setText("-");
        }
    }

    @FXML
    private void fechar() {
        if (navigator != null) {
            navigator.closeStage(atendimentoVbox);
        }
    }

    private String formatarData(LocalDateTime data) {
        if (data == null) return "Aguardando...";
        return dtf.format(data);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
