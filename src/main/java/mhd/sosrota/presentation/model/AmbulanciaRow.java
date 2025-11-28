package mhd.sosrota.presentation.model;

import javafx.beans.property.*;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 28/11/2025
 * @brief Class AmbulanciaRow
 */
public class AmbulanciaRow {
    private final LongProperty id;
    private final StringProperty placa;
    private final ObjectProperty<TipoAmbulancia> tipo;
    private final ObjectProperty<StatusAmbulancia> status;
    private final ObjectProperty<Bairro> bairroBase;

    public AmbulanciaRow(Ambulancia a) {
        this.id = new SimpleLongProperty(a.getId());
        this.placa = new SimpleStringProperty(a.getPlaca());
        this.tipo = new SimpleObjectProperty<>(a.getTipoAmbulancia());
        this.status = new SimpleObjectProperty<>(a.getStatusAmbulancia());
        this.bairroBase = new SimpleObjectProperty<>(a.getBairroBase());
    }

    public Ambulancia toEntity() {
        Ambulancia a = new Ambulancia();
        a.setId(getId());
        a.setPlaca(getPlaca());
        a.setTipoAmbulancia(TipoAmbulancia.fromDescricao(getTipo()));
        a.setStatusAmbulancia(StatusAmbulancia.fromDescricao(getTipo()));
        a.setBairroBase(getBairroBase());
        return a;
    }

    public long getId() { return id.get(); }
    public String getPlaca() { return placa.get(); }
    public String getTipo() { return tipo.get().getDescricao(); }
    public String getStatus() { return status.get().getDescricao(); }
    public Bairro getBairroBase() { return bairroBase.get(); }

    public LongProperty idProperty() { return id; }
    public StringProperty placaProperty() { return placa; }
    public ObjectProperty<TipoAmbulancia> tipoAmbulanciaProperty() { return tipo; }
    public ObjectProperty<StatusAmbulancia> statusAmbulanciaProperty() { return status; }
    public ObjectProperty<Bairro> bairroBaseProperty() { return bairroBase; }
}
