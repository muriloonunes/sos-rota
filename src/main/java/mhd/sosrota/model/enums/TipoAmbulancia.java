package mhd.sosrota.model.enums;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Enum TipoAmbulancia
 */
public enum TipoAmbulancia {
    UTI("UTI"),
    BASICA("Basica");
    
    private final String descricao;

    TipoAmbulancia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
