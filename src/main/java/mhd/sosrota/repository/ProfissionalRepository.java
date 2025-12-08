package mhd.sosrota.repository;

import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;

import java.util.List;

/**
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 27/11/2025
 * @brief class AmbulanciaRepositoryImpl
 */

public interface ProfissionalRepository {
    boolean salvar(Profissional profissional);

    Profissional buscarPorNome(String nome);

    Profissional buscarPorId(Long id);

    List<Profissional> listarTodos();

    List<Profissional> listarPorFuncao(FuncaoProfissional funcao);

    List<Profissional> listarDisponiveis();

    Profissional atualizar(Profissional profissional);

    boolean deletar(Profissional profissional);

    //List<Profissional> listarPorEquipe(Long equipeId);
}
