package mhd.sosrota.service;

import mhd.sosrota.model.Usuario;
import mhd.sosrota.model.exceptions.AuthenticationException;
import mhd.sosrota.repository.UsuarioRepository;
import mhd.sosrota.util.PasswordUtil;
import org.postgresql.util.PSQLException;

import java.sql.SQLException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Class UsuarioService
 */
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticar(String username, String senha) throws SQLException, AuthenticationException {
        Usuario usuarioEncontrado = usuarioRepository.encontrarPorUsername(username);
        if (usuarioEncontrado == null) {
            throw new AuthenticationException("Usuário ou senha inválidos");
        }
        String hashDigitado = PasswordUtil.criarHash(senha);
        if (!hashDigitado.equals(usuarioEncontrado.getSenha())) {
            throw new AuthenticationException("Usuário ou senha inválidos");
        }
        return usuarioEncontrado;
    }

    public boolean cadastrarUsuario(String nome, String username, String senha) throws SQLException, AuthenticationException {
        try {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setUsername(username);
            String hashSenha = PasswordUtil.criarHash(senha);
            novoUsuario.setSenha(hashSenha);
            return usuarioRepository.salvar(novoUsuario);
        } catch (PSQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new AuthenticationException("Esse nome de usuário já está em uso.");
            } else {
                throw e;
            }
        }
    }
}
