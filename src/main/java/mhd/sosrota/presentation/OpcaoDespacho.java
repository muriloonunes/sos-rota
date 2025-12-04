package mhd.sosrota.presentation;

import mhd.sosrota.model.Ambulancia;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 04/12/2025
 * @brief Class OpcaoDespacho
 */
public class OpcaoDespacho {
    private Ambulancia ambulancia;
    private double distanciaKm; // Calculado pelo Dijkstra
    private double tempoEstimadoMin; // Calculado pelo Dijkstra

    public OpcaoDespacho(Ambulancia ambulancia, double distanciaKm, double tempoEstimadoMin) {
        this.ambulancia = ambulancia;
        this.distanciaKm = distanciaKm;
        this.tempoEstimadoMin = tempoEstimadoMin;
    }

    public Ambulancia getAmbulancia() { return ambulancia; }
    public String getPlaca() { return ambulancia.getPlaca(); }
    public String getTipo() { return ambulancia.getTipoAmbulancia().toString(); }
    public String getLocalAtual() { return ambulancia.getBairroBase().getNome(); }

    public String getDistanciaFormatada() {
        return String.format("%.1f km", distanciaKm);
    }
    public String getTempoFormatado() {
        return String.format("%.0f min", tempoEstimadoMin);
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public double getTempoEstimadoMin() { return tempoEstimadoMin; }
}
