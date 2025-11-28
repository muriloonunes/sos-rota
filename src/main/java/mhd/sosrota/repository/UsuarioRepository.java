package mhd.sosrota.repository;

import mhd.sosrota.model.Usuario;

import java.util.List;

/**
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 25/11/2025
 * @brief Interface UsuarioRepository
 */
public interface UsuarioRepository {
    Usuario encontrarPorUsername(String username);

    List<Usuario> listarTodosUsuários();

    boolean salvar(Usuario usuario);

    Usuario atualizarUsuario(Usuario usuario);

    boolean deletarUsuario(Usuario usuario);
}
