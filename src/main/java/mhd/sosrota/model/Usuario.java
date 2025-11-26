package mhd.sosrota.model;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Class Usuario
 */
public class Usuario {
    private String nome;
    private String username;
    private String senha;

    public Usuario() {
    }

    public Usuario(String nome, String username) {
        this.nome = nome;
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", username='" + username + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}
