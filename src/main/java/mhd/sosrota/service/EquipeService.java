package mhd.sosrota.service;

import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.repository.AmbulanciaRepository;
import mhd.sosrota.repository.EquipeRepository;

import java.util.List;

public class EquipeService {
    private final EquipeRepository repo;
    private final AmbulanciaRepository ambulanciaRepository;

    public EquipeService(EquipeRepository repo, AmbulanciaRepository ambulanciaRepository) {
        this.repo = repo;
        this.ambulanciaRepository = ambulanciaRepository;
    }

    public boolean cadastrarEquipe(Equipe equipe) {
        if (!validarComposicao(equipe)) {
            throw new CadastroException("Uma ambulância do tipo UTI deve ter um médico");
        }
        Ambulancia amb = ambulanciaRepository.encontrarPorId(equipe.getAmbulancia().getId());
        if (amb.getStatusAmbulancia() == StatusAmbulancia.DISPONIVEL || amb.getStatusAmbulancia() == StatusAmbulancia.EM_ATENDIMENTO) {
            throw new CadastroException("Esta ambulância já possui uma equipe ativa ou está em atendimento.");
        }
        return repo.insertEquipe(equipe);
    }

    public boolean atualizarEquipe(Equipe equipe) {
        if (!validarComposicao(equipe)) {
            throw new CadastroException("Uma ambulância do tipo UTI deve ter um médico");
        }

        if (equipe.isAtivo()) {
            boolean ambulanciaOcupada = repo.existeEquipeComAmbulancia(
                    equipe.getAmbulancia().getId(),
                    equipe.getId()
            );

            if (ambulanciaOcupada) {
                equipe.setAtivo(false);
                throw new CadastroException(
                        "Não é possível reativar esta equipe. A ambulância " +
                                equipe.getAmbulancia().getPlaca() +
                                " já está em uso por outra equipe ativa."
                );
            }
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
