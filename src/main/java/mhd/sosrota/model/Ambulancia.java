package mhd.sosrota.model;

import jakarta.persistence.*;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAmbulancia tipoAmbulancia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAmbulancia statusAmbulancia;
    //TODO chave estrangeira bairroBase
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "bairro_base_id", nullable = false)
//    private Bairro bairroBase;

    public Ambulancia( StatusAmbulancia statusAmbulancia, TipoAmbulancia tipoAmbulancia, String placa) {
        this.statusAmbulancia = statusAmbulancia;
        this.tipoAmbulancia = tipoAmbulancia;
        this.placa = placa;
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
}
