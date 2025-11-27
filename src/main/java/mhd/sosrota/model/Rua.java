package mhd.sosrota.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ruas")
public class Rua {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aresta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bairro_origem", nullable = false)
    private Bairro origem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bairro_destino", nullable = false)
    private Bairro destino;

    @Column(name = "distancia_km")
    private double distanciaKm;

    public Rua() {}

    public Rua(Long id, Bairro origem, Bairro destino, double distanciaKm) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bairro getOrigem() {
        return origem;
    }

    public void setOrigem(Bairro origem) {
        this.origem = origem;
    }

    public Bairro getDestino() {
        return destino;
    }

    public void setDestino(Bairro destino) {
        this.destino = destino;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
}
