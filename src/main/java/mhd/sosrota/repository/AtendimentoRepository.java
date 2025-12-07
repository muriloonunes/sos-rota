package mhd.sosrota.repository;

import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.GravidadeOcorrencia;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 04/12/2025
 * @brief Interface AtendimentoRepository
 */
public interface AtendimentoRepository {
    Atendimento salvar(Atendimento atendimento, Ocorrencia ocorrencia, Ambulancia ambulancia);

    List<Atendimento> consultarHistorico(
            Long ambulanciaId,
            LocalDateTime inicio,
            LocalDateTime fim,
            GravidadeOcorrencia gravidade
    );

    Atendimento buscarPorOcorrenciaId(Long ocorrenciaId);

    Double tempoMedioRespostaPorGravidade(GravidadeOcorrencia gravidade);

    List<Object[]> mapaOcorrenciasPorBairro();
}
