package mhd.sosrota.repository;

import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.Ocorrencia;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 04/12/2025
 * @brief Interface AtendimentoRepository
 */
public interface AtendimentoRepository {
    Atendimento salvar(Atendimento atendimento, Ocorrencia ocorrencia, Ambulancia ambulancia);
}
