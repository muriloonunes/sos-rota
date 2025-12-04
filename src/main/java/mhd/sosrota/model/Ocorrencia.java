package mhd.sosrota.model;

import jakarta.persistence.*;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Class Ocorrencia
 */
@Entity
@Table(name = "ocorrencias")
public class Ocorrencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ocorrencia")
    private Long id;

    @Column(name = "tipo_ocorrencia", nullable = false, length = 50)
    private String tipoOcorrencia;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "gravidade", nullable = false)
    private GravidadeOcorrencia gravidadeOcorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_bairro_id", nullable = false)
    private Bairro bairro;

    @Column(name = "data_hora_abertura", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime dataHoraAbertura;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    private StatusOcorrencia statusOcorrencia;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    public Ocorrencia() {
    }

    public Ocorrencia(Long id,
                      String tipoOcorrencia,
                      GravidadeOcorrencia gravidadeOcorrencia,
                      Bairro bairro,
                      OffsetDateTime dataHoraAbertura,
                      StatusOcorrencia statusOcorrencia,
                      String observacao) {
        this.id = id;
        this.tipoOcorrencia = tipoOcorrencia;
        this.gravidadeOcorrencia = gravidadeOcorrencia;
        this.bairro = bairro;
        this.dataHoraAbertura = dataHoraAbertura;
        this.statusOcorrencia = statusOcorrencia;
        this.observacao = observacao;
    }

    public OffsetDateTime getLimiteSLA() {
        if (dataHoraAbertura == null || gravidadeOcorrencia == null) {
            return null;
        }
        return dataHoraAbertura.plusMinutes(gravidadeOcorrencia.getTempoSLA());
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

    public OffsetDateTime getDataHoraAbertura() {
        return dataHoraAbertura;
    }

    public void setDataHoraAbertura(OffsetDateTime dataHoraOcorrencia) {
        this.dataHoraAbertura = dataHoraOcorrencia;
    }

    public String getTipoOcorrencia() {
        return tipoOcorrencia;
    }

    public void setTipoOcorrencia(String tipoOcorrencia) {
        this.tipoOcorrencia = tipoOcorrencia;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
