package mhd.sosrota.model;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 04/12/2025
 * @brief Class Atendimento
 */

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "atendimentos")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atendimento")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ocorrencia_id", referencedColumnName = "id_ocorrencia", nullable = false)
    private Ocorrencia ocorrencia;

    @ManyToOne
    @JoinColumn(name = "ambulancia_id", referencedColumnName = "id_ambulancia", nullable = false)
    private Ambulancia ambulancia;

    @Column(name = "data_hora_despacho", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataHoraDespacho;

    @Column(name = "data_hora_chegada")
    private LocalDateTime dataHoraChegada;

    @Column(name = "data_hora_conclusao")
    private LocalDateTime dataHoraConclusao;

    @Column(name = "distancia_km", nullable = false)
    private Double distanciaKm;

    public Atendimento() {}

    public Atendimento(Ocorrencia ocorrencia, Ambulancia ambulancia, Double distanciaKm) {
        this.ocorrencia = ocorrencia;
        this.ambulancia = ambulancia;
        this.distanciaKm = distanciaKm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ocorrencia getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Ocorrencia ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public Ambulancia getAmbulancia() {
        return ambulancia;
    }

    public void setAmbulancia(Ambulancia ambulancia) {
        this.ambulancia = ambulancia;
    }

    public LocalDateTime getDataHoraDespacho() {
        return dataHoraDespacho;
    }

    public void setDataHoraDespacho(LocalDateTime dataHoraDespacho) {
        this.dataHoraDespacho = dataHoraDespacho;
    }

    public LocalDateTime getDataHoraChegada() {
        return dataHoraChegada;
    }

    public void setDataHoraChegada(LocalDateTime dataHoraChegada) {
        this.dataHoraChegada = dataHoraChegada;
    }

    public LocalDateTime getDataHoraConclusao() {
        return dataHoraConclusao;
    }

    public void setDataHoraConclusao(LocalDateTime dataHoraConclusao) {
        this.dataHoraConclusao = dataHoraConclusao;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
}