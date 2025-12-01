package mhd.sosrota.service;

import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.enums.TipoAmbulancia;
import mhd.sosrota.repository.EquipeRepository;

import java.util.List;

public class EquipeService {

    private final EquipeRepository equipeRepository;

    public EquipeService(EquipeRepository equipeRepository) {
        this.equipeRepository = equipeRepository;
    }

    public boolean salvarEquipe(Equipe equipe) {

        if (!validarComposicao(equipe)) {
            return false; // regra de negócio não cumprida
        }

        return equipeRepository.insertEquipe(equipe);
    }

    public boolean atualizarEquipe(Equipe equipe) {

        if (!validarComposicao(equipe)) {
            return false;
        }

        return equipeRepository.updateEquipe(equipe);
    }

    public boolean removerEquipe(Equipe equipe) {
        return equipeRepository.deleteEquipe(equipe);
    }

    public List<Equipe> listarEquipes() {
        return equipeRepository.findAllEquipes();
    }

    public Equipe buscarPorPlaca(String placa) {
        return equipeRepository.buscaPorPlacaAmbulancia(placa);
    }

    public List<Equipe> buscarPorNomeProfissional(String nome) {
        return equipeRepository.buscaPorNomeProfissional(nome);
    }

    /**
     * Valida a composição mínima da equipe conforme o tipo da ambulância.
     */
    private boolean validarComposicao(Equipe equipe) {

        List<Profissional> profissionais = equipe.getProfissionais();

        long medicos = profissionais.stream().filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.MEDICO).count();
        long enfermeiros = profissionais.stream().filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.ENFERMEIRO).count();
        long condutores = profissionais.stream().filter(p -> p.getFuncaoProfissional() == FuncaoProfissional.CONDUTOR).count();

        TipoAmbulancia tipo = equipe.getAmbulancia().getTipoAmbulancia();

        if (tipo == TipoAmbulancia.UTI) {

            return medicos >= 1 &&
                    enfermeiros >= 1 &&
                    condutores >= 1;

        } else {

            return enfermeiros >= 1 &&
                    condutores >= 1;
        }
    }
}
