package mhd.sosrota.service;

import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.GrafoBairro;
import mhd.sosrota.model.Rua;
import mhd.sosrota.repository.BairroRepository;
import mhd.sosrota.repository.RuaRepository;

import java.util.List;

public class GrafoBairroService {
    private final BairroRepository bairroRepository;
    private final RuaRepository ruaRepository;

    private GrafoBairro grafo;

    public GrafoBairroService(BairroRepository bairroRepository, RuaRepository ruaRepository) {
        this.bairroRepository = bairroRepository;
        this.ruaRepository = ruaRepository;
    }

    public GrafoBairro obterGrafo() {
        if (this.grafo == null) {
            List<Bairro> bairros = bairroRepository.obterBairros();
            List<Rua> ruas = ruaRepository.obterRuas();
            this.grafo = new GrafoBairro(bairros, ruas);
        }
        return this.grafo;
    }

    public List<Bairro> obterBairros() {
        return obterGrafo().getBairros();
    }

    public List<Rua> obterRuas() {
        return obterGrafo().getRuas();
    }
}
