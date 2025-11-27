package mhd.sosrota.model.enums;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Enum StatusAmbulancia
 */
public enum StatusAmbulancia {
    DISPONIVEL("Disponível"),
    EM_ATENDIMENTO("Em Atendimento"),
    MANUTENCAO("Manutenção"),
    INATIVA("Inativa");

    private final String descricao;

    StatusAmbulancia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
