package mhd.sosrota.repository;

import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.enums.StatusAmbulancia;

import java.util.List;

/**
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 27/11/2025
 * @brief Interface AmbulanciaRepository
 */

public interface AmbulanciaRepository {
    Ambulancia encontrarPorId(long id);

    Ambulancia encontrarPorIdComBairro(long id);

    List<Ambulancia> listarTodasAmbulancias();

    List<Ambulancia> listarAmbulanciaSemEquipe();

    void salvar(Ambulancia ambulancia);

    void atualizarAmbulancia(Ambulancia ambulancia);

    boolean deletarAmbulancia(long id);

    long obterAmbulanciaStatus(StatusAmbulancia status);
}
