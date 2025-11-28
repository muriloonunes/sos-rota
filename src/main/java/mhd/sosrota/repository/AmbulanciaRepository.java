package mhd.sosrota.repository;

import mhd.sosrota.model.Ambulancia;

import java.util.List;

/**
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 27/11/2025
 * @brief Interface AmbulanciaRepository
 */

public interface AmbulanciaRepository {
    Ambulancia encontrarPorPlaca(String placa);

    List<Ambulancia> listarTodasAmbulancias();

    boolean salvar(Ambulancia ambulancia);

    Ambulancia atualizarAmbulancia(Ambulancia ambulancia);

    boolean deletarAmbulancia(Ambulancia ambulancia);
}
