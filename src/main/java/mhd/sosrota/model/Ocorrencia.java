package mhd.sosrota.model;

import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;

import java.time.LocalDateTime;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Class Ocorrencia
 */
public class Ocorrencia {
    private StatusOcorrencia statusOcorrencia;
    private GravidadeOcorrencia gravidadeOcorrencia;
    private LocalDateTime dataHoraOcorrencia;
    private String tipoOcorrencia;
    //private String descricaoOcorrencia;

    public Ocorrencia(StatusOcorrencia statusOcorrencia, GravidadeOcorrencia gravidadeOcorrencia, LocalDateTime dataHoraOcorrencia, String tipoOcorrencia) {
        this.statusOcorrencia = statusOcorrencia;
        this.gravidadeOcorrencia = gravidadeOcorrencia;
        this.dataHoraOcorrencia = dataHoraOcorrencia;
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public Ocorrencia(){}

    public StatusOcorrencia getStatusOcorrencia() {
        return statusOcorrencia;
    }

    public void setStatusOcorrencia(StatusOcorrencia statusOcorrencia) {
        this.statusOcorrencia = statusOcorrencia;
    }

    public GravidadeOcorrencia getGravidadeOcorrencia() {
        return gravidadeOcorrencia;
    }

    public void setGravidadeOcorrencia(GravidadeOcorrencia gravidadeOcorrencia) {
        this.gravidadeOcorrencia = gravidadeOcorrencia;
    }

    public LocalDateTime getDataHoraOcorrencia() {
        return dataHoraOcorrencia;
    }

    public void setDataHoraOcorrencia(LocalDateTime dataHoraOcorrencia) {
        this.dataHoraOcorrencia = dataHoraOcorrencia;
    }

    public String getTipoOcorrencia() {
        return tipoOcorrencia;
    }

    public void setTipoOcorrencia(String tipoOcorrencia) {
        this.tipoOcorrencia = tipoOcorrencia;
    }
}
