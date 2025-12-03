package mhd.sosrota.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Class Equipe
 */
@Entity
@Table(name = "equipes")
public class Equipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipe")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambulancia_id", nullable = false)
    private Ambulancia ambulancia;

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "equipe", fetch = FetchType.LAZY)
    private List<Profissional> profissionais = new ArrayList<>();

    public Equipe() {
    }

    public Equipe(Long id, Ambulancia ambulancia, boolean ativo, List<Profissional> profissionais) {
        this.id = id;
        this.ambulancia = ambulancia;
        this.ativo = ativo;
        this.profissionais = profissionais;
    }

    public void addProfissional(Profissional p) {
        if (p != null) {
            profissionais.add(p);
            p.setEquipe(this);
        }
    }

    public void removeProfissional(Profissional p) {
        profissionais.remove(p);
        p.setEquipe(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ambulancia getAmbulancia() {
        return ambulancia;
    }

    public void setAmbulancia(Ambulancia ambulancia) {
        this.ambulancia = ambulancia;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Profissional> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(List<Profissional> profissionais) {
        this.profissionais = profissionais;
    }
}
