package mhd.sosrota.repository;

import mhd.sosrota.model.Usuario;

import java.sql.SQLException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Interface UsuarioRepository
 */
public interface UsuarioRepository {
    Usuario encontrarPorUsername(String username) throws SQLException;
    boolean salvar(Usuario usuario) throws SQLException;
}
