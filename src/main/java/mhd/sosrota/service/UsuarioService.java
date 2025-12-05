package mhd.sosrota.service;

import mhd.sosrota.infrastructure.UserPrefs;
import mhd.sosrota.model.Usuario;
import mhd.sosrota.model.exceptions.AuthenticationException;
import mhd.sosrota.repository.UsuarioRepository;
import mhd.sosrota.util.PasswordUtil;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Class UsuarioService
 */
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UserPrefs prefs = new UserPrefs();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario autenticar(String username, String senha) throws AuthenticationException {
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

    public boolean cadastrarUsuario(String nome, String username, String senha) throws AuthenticationException {
        if (username.length() > 20) {
            throw new AuthenticationException("O nome de usuário não pode ter mais de 20 caracteres");
        }
        if (nome.length() > 50) {
            throw new AuthenticationException("O nome não pode ter mais de 50 caracteres");
        }
        try {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setUsername(username);
            String hashSenha = PasswordUtil.criarHash(senha);
            novoUsuario.setSenha(hashSenha);
            usuarioRepository.salvar(novoUsuario);
            return true;
        } catch (Exception e) {
            Throwable causaAtual = e;
            while (causaAtual != null) {
                if (causaAtual instanceof ConstraintViolationException) {
                    throw new AuthenticationException("Esse nome de usuário já está em uso.");
                }
                if (causaAtual instanceof SQLException) {
                    if ("23505".equals(((SQLException) causaAtual).getSQLState())) {
                        throw new AuthenticationException("Esse nome de usuário já está em uso.");
                    }
                }
                causaAtual = causaAtual.getCause();
            }

            throw e;
        }
    }

    public void salvarUsuario(String nome, String username) {
        prefs.salvarUsuario(nome, username);
    }

    public Usuario obterUsuarioSalvo() {
        String nome = prefs.getNome();
        String username = prefs.getUsername();
        return new Usuario(nome, username);
    }

    public void limparDados() {
        prefs.limparDados();
    }

    public void redefinirSenha(String username, String novaSenha) throws AuthenticationException {
        if (username == null || username.isBlank()) {
            throw new AuthenticationException("Informe o nome de usuário.");
        }
        if (novaSenha == null || novaSenha.isBlank()) {
            throw new AuthenticationException("A nova senha não pode ser vazia.");
        }
        Usuario usuario = usuarioRepository.encontrarPorUsername(username);
        if (usuario == null) {
            throw new AuthenticationException("Usuário não encontrado.");
        }
        String hash = PasswordUtil.criarHash(novaSenha);
        usuario.setSenha(hash);
        usuarioRepository.atualizarUsuario(usuario);
    }

}
