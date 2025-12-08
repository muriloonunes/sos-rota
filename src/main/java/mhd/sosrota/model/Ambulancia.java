package mhd.sosrota.model;

import jakarta.persistence.*;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 26/11/2025
 * @brief Class Ambulancia
 */
@Entity
@Table(name = "ambulancias")
public class Ambulancia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ambulancia")
    private Long id;

    @Column(nullable = false, unique = true, length = 7)
    private String placa;

    @Enumerated()
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "tipo", nullable = false)
    private TipoAmbulancia tipoAmbulancia;

    @Enumerated()
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false)
    private StatusAmbulancia statusAmbulancia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bairro_base_id", nullable = false)
    private Bairro bairroBase;

    public Ambulancia(StatusAmbulancia statusAmbulancia, TipoAmbulancia tipoAmbulancia, String placa, Bairro bairroBase) {
        this.statusAmbulancia = statusAmbulancia;
        this.tipoAmbulancia = tipoAmbulancia;
        this.placa = placa;
        this.bairroBase = bairroBase;
    }

    public Ambulancia() {
    }

    public StatusAmbulancia getStatusAmbulancia() {
        return statusAmbulancia;
    }

    public void setStatusAmbulancia(StatusAmbulancia statusAmbulancia) {
        this.statusAmbulancia = statusAmbulancia;
    }

    public TipoAmbulancia getTipoAmbulancia() {
        return tipoAmbulancia;
    }

    public void setTipoAmbulancia(TipoAmbulancia tipoAmbulancia) {
        this.tipoAmbulancia = tipoAmbulancia;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bairro getBairroBase() {
        return bairroBase;
    }

    public void setBairroBase(Bairro bairroBase) {
        this.bairroBase = bairroBase;
    }

    @Override
    public String toString() {
        return "Ambulancia{" +
                "id=" + id +
                ", placa='" + placa + '\'' +
                ", tipoAmbulancia=" + tipoAmbulancia +
                ", statusAmbulancia=" + statusAmbulancia +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ambulancia that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
