package mhd.sosrota.service;

import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.enums.TipoAmbulancia;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.repository.EquipeRepository;

import java.util.List;

public class EquipeService {
    private final EquipeRepository repo;

    public EquipeService(EquipeRepository repo) {
        this.repo = repo;
    }

    public boolean cadastrarEquipe(Equipe equipe) {
        if (!validarComposicao(equipe)) {
            throw new CadastroException("Uma ambulância do tipo UTI deve ter um médico");
        }
        return repo.insertEquipe(equipe);
    }

    public boolean atualizarEquipe(Equipe equipe) {
        if (!validarComposicao(equipe)) {
            throw new CadastroException("Uma ambulância do tipo UTI deve ter um médico");
        }

        return repo.updateEquipe(equipe);
    }

    public boolean removerEquipe(long id) {
        return repo.deleteEquipe(id);
    }

    public List<Equipe> listarEquipes() {
        return repo.findAllEquipes();
    }

    public Equipe buscarPorPlaca(String placa) {
        return repo.buscaPorPlacaAmbulancia(placa);
    }

    public List<Equipe> buscarPorNomeProfissional(String nome) {
        return repo.buscaPorNomeProfissional(nome);
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
