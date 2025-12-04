package mhd.sosrota.repository;

import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;

import java.time.LocalDateTime;
import java.util.List;

public interface OcorrenciaRepository {

    Boolean salvar(Ocorrencia ocorrencia);

    Ocorrencia buscarPorId(Long id);

    List<Ocorrencia> buscarPorStatus(StatusOcorrencia status);

    List<Ocorrencia> buscarPorTipo(String tipo);

    List<Ocorrencia> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    List<Ocorrencia> listarTodas();

    Boolean atualizar(Ocorrencia ocorrencia);

    Boolean deletar(Long id);
}
