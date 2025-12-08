package mhd.sosrota.repository;

import mhd.sosrota.model.Equipe;

import java.util.List;

public interface EquipeRepository {
    Boolean insertEquipe(Equipe equipe);

    Equipe buscaPorAmbulancia(Long id);

    List<Equipe> buscaPorNomeProfissional(String nome);

    List<Equipe> findAllEquipes();

    Boolean updateEquipe(Equipe equipe);

    Boolean deleteEquipe(long id);

    boolean existeEquipeComAmbulancia(long idAmbulancia, long idEquipeIgnorada);
}
