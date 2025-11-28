package mhd.sosrota.model;

import jakarta.persistence.*;
import mhd.sosrota.model.enums.FuncaoProfissional;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.io.Serializable;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 26/11/2025
 * @brief Class Profissional
 */
@Entity
@Table(name = "profissionais")
public class Profissional implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profissional")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated()
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "funcao", nullable = false)
    private FuncaoProfissional funcaoProfissional;

    @Column(name = "email_contato", length = 50)
    private String contato;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    public Profissional() {
    }

    public Profissional(long id, String nome, FuncaoProfissional funcaoProfissional, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.funcaoProfissional = funcaoProfissional;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public FuncaoProfissional getFuncaoProfissional() {
        return funcaoProfissional;
    }

    public void setFuncaoProfissional(FuncaoProfissional funcaoProfissional) {
        this.funcaoProfissional = funcaoProfissional;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }
}
