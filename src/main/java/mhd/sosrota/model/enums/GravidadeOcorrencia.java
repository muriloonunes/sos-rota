package mhd.sosrota.model.enums;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Class Gravidade
 */
public enum GravidadeOcorrencia {
    ALTA("Alta"),
    MEDIA("Media"),
    BAIXA("Baixa");

    private final String descricao;

    GravidadeOcorrencia(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
