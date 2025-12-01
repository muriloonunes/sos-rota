package mhd.sosrota.repository;

import mhd.sosrota.model.Ocorrencia;

import java.time.LocalDateTime;
import java.util.List;

public interface OcorrenciaRepository {

    Boolean salvar(Ocorrencia ocorrencia);

    Ocorrencia buscarPorId(Long id);

    List<Ocorrencia> buscarPorStatus(String status);

    List<Ocorrencia> buscarPorTipo(String tipo);

    List<Ocorrencia> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    List<Ocorrencia> listarTodas();

    Boolean atualizar(Ocorrencia ocorrencia);

    Boolean deletar(Long id);
}
