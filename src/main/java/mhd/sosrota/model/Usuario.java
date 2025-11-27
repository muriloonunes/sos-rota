package mhd.sosrota.model;

import jakarta.persistence.*;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 25/11/2025
 * @brief Class Usuario
 */
@Entity
@Table(name = "users")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome", length = 50, nullable = false)
    private String nome;
    @Column(name = "username", length = 20, nullable = false, unique = true)
    private String username;
    @Column(name = "senha", nullable = false)
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
        return "User [id=" + id + ", username=" + username + ", nome=" + nome + "]";
    }
}
