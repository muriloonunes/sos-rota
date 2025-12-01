package mhd.sosrota.model;

import jakarta.persistence.*;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;

import java.time.LocalDateTime;

@Entity
@Table(name = "ocorrencias")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ocorrencia")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOcorrencia statusOcorrencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GravidadeOcorrencia gravidadeOcorrencia;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHoraOcorrencia;

    @Column(name = "tipo_ocorrencia", nullable = false)
    private String tipoOcorrencia;

    public Ocorrencia() {}

    public Ocorrencia(StatusOcorrencia statusOcorrencia,
                      GravidadeOcorrencia gravidadeOcorrencia,
                      LocalDateTime dataHoraOcorrencia,
                      String tipoOcorrencia) {

        this.statusOcorrencia = statusOcorrencia;
        this.gravidadeOcorrencia = gravidadeOcorrencia;
        this.dataHoraOcorrencia = dataHoraOcorrencia;
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public Long getId() {
        return id;
    }

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
