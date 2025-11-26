package mhd.sosrota.repository;

import mhd.sosrota.infrastructure.database.DatabaseConnection;
import mhd.sosrota.model.Usuario;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Class UsuarioRepository
 */
public class UsuarioRepositoryImpl implements UsuarioRepository {
    @Override
    public Usuario encontrarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        Usuario usuario = null;
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setNome(rs.getString("nome"));
                usuario.setUsername(rs.getString("username"));
                usuario.setSenha(rs.getString("senha"));
                return usuario;
            }
        }
        return usuario;
    }

    @Override
    public boolean salvar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO users (nome, username, senha) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getUsername());
            stmt.setString(3, usuario.getSenha());
            stmt.executeUpdate();
            return true;
        }
    }
}
