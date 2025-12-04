package mhd.sosrota.model.enums;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Class Gravidade
 */
public enum GravidadeOcorrencia {
    ALTA("Alta", 8),
    MEDIA("Media", 15),
    BAIXA("Baixa", 30);

    private final String descricao;
    private final int tempoSLA;

    GravidadeOcorrencia(String descricao, int tempoSLa) {
        this.descricao = descricao;
        this.tempoSLA = tempoSLa;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getTempoSLA() {
        return tempoSLA;
    }
}
