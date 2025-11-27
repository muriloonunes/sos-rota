package mhd.sosrota.model.enums;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 26/11/2025
 * @brief Enum Funcao
 */
public enum FuncaoProfissional {
    MEDICO("Médico"),
    ENFERMEIRO("Enfermeiro"),
    CONDUTOR("Condutor");

    private final String nome;

    FuncaoProfissional(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
