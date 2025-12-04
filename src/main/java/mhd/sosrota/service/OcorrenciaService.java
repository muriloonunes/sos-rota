package mhd.sosrota.service;

import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.repository.OcorrenciaRepository;

import java.util.List;

public class OcorrenciaService {
    private final OcorrenciaRepository repository;

    public OcorrenciaService(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public List<Ocorrencia> listarTodas() {
        return repository.listarTodas();
    }

    public void salvar(Ocorrencia ocorrencia) {
        if (ocorrencia.getTipoOcorrencia() == null || ocorrencia.getTipoOcorrencia().trim().isEmpty()) {
            throw new CadastroException("O tipo da ocorrência é obrigatório");
        }
        if (ocorrencia.getGravidadeOcorrencia() == null) {
            throw new CadastroException("A gravidade da ocorrência deve ser informada.");
        }

        if (ocorrencia.getBairro() == null) {
            throw new CadastroException("O local da ocorrência é obrigatório.");
        }
        if (ocorrencia.getId() == null) {
            ocorrencia.setStatusOcorrencia(StatusOcorrencia.ABERTA);
        }
        if (StatusOcorrencia.CONCLUIDA.equals(ocorrencia.getStatusOcorrencia())
                && ocorrencia.getId() == null) {
            throw new CadastroException("Não é possível criar uma ocorrência já concluída.");
        }
        repository.salvar(ocorrencia);
    }

    public void deletar(Long id) {
        repository.deletar(id);
    }
}
