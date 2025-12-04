package mhd.sosrota.service;

import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.repository.OcorrenciaRepository;

public class OcorrenciaService {

    private final OcorrenciaRepository repository;

    public OcorrenciaService(OcorrenciaRepository repository) {
        this.repository = repository;
    }

    public boolean salvar(Ocorrencia ocorrencia) {
        return repository.salvar(ocorrencia);
    }
}
