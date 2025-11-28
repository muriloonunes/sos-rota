package mhd.sosrota.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bairros")
public class Bairro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bairro")
    private Long id;

    @Column(name = "nome_bairro", nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "tem_base", nullable = false)
    private Boolean temBase;

    @Column(name = "pos_x", nullable = false)
    private double x;

    @Column(name = "pos_y", nullable = false)
    private double y;

    public Bairro() {
    }

    public Bairro(Long id, String nome, boolean temBase, double x, double y) {
        this.id = id;
        this.nome = nome;
        this.temBase = temBase;
        this.x = x;
        this.y = y;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isTemBase() {
        return temBase;
    }

    public void setTemBase(boolean temBase) {
        this.temBase = temBase;
    }

    public Boolean temBase() {
        return temBase;
    }

    public void setTemBase(Boolean temBase) {
        this.temBase = temBase;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bairro other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public String toString() {
        return nome;
    }
}
