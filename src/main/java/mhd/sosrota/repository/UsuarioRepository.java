package mhd.sosrota.repository;

import mhd.sosrota.model.Usuario;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Interface UsuarioRepository
 */
public interface UsuarioRepository {
    Usuario encontrarPorUsername(String username);
    boolean salvar(Usuario usuario);
}
