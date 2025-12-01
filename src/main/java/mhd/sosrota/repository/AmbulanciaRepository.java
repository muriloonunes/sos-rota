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
    Ambulancia encontrarPorPlaca(String placa);

    List<Ambulancia> listarTodasAmbulancias();

    List<Ambulancia> listarDisponiveis();

    void salvar(Ambulancia ambulancia);

    Ambulancia atualizarAmbulancia(Ambulancia ambulancia);

    boolean deletarAmbulancia(long id);

    long obterAmbulanciaStatus(StatusAmbulancia status);
}
