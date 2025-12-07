package mhd.sosrota.model.enums;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Enum StatusOcorrencia
 */
public enum StatusOcorrencia {
    ABERTA("Aberta"),
    DESPACHADA("Despachada"),
    EM_ATENDIMENTO("Em Atendimento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusOcorrencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    // ===== MÉTODOS UTILITÁRIOS ===== //

    /** Retorna true se a ocorrência ainda não foi atendida */
    public boolean isAberta() {
        return this == ABERTA;
    }

    /** Retorna true se já foi despachada para uma equipe */
    public boolean isDespachada() {
        return this == DESPACHADA;
    }

    /** Retorna true se está em processo de atendimento */
    public boolean isEmAtendimento() {
        return this == EM_ATENDIMENTO;
    }

    /** Retorna true se está concluída ou cancelada */
    public boolean isFinalizada() {
        return this == CONCLUIDA || this == CANCELADA;
    }

    /** Retorna true se ainda pode sofrer alterações */
    public boolean isEditavel() {
        return this == ABERTA || this == DESPACHADA || this == EM_ATENDIMENTO;
    }

    /** Apenas concluída com sucesso (não cancelada) */
    public boolean isConcluidaComSucesso() {
        return this == CONCLUIDA;
    }

    /** Retorna true se está encerrada definitivamente (concluída ou cancelada) */
    public boolean isEncerrada() {
        return this.isFinalizada();
    }
}
