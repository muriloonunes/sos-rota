package mhd.sosrota.presentation.model;

import javafx.beans.property.*;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 30/11/2025
 * @brief Class ProfissionalRow
 */
public class ProfissionalRow {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty nome = new SimpleStringProperty();
    private final StringProperty funcao = new SimpleStringProperty();
    private final StringProperty contato = new SimpleStringProperty();
    private final BooleanProperty ativo = new SimpleBooleanProperty();

    public ProfissionalRow() {}

    public ProfissionalRow(Profissional p) {
        setId(p.getId());
        setNome(p.getNome());
        setFuncao(p.getFuncaoProfissional().getNome());
        setContato(p.getContato());
        setAtivo(p.isAtivo());
    }

    public Profissional toProfissional() {
        Profissional p = new Profissional();
        p.setId(this.getId());
        p.setNome(this.getNome());
        p.setContato(this.getContato());
        p.setAtivo(this.isAtivo());

        p.setFuncaoProfissional(FuncaoProfissional.fromNome(this.getFuncao()));

        return p;
    }

    public long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }
    public void setId(long id) { this.id.set(id); }

    public String getNome() { return nome.get(); }
    public StringProperty nomeProperty() { return nome; }
    public void setNome(String nome) { this.nome.set(nome); }

    public String getFuncao() { return funcao.get(); }
    public StringProperty funcaoProperty() { return funcao; }
    public void setFuncao(String funcao) { this.funcao.set(funcao); }

    public String getContato() { return contato.get(); }
    public StringProperty contatoProperty() { return contato; }
    public void setContato(String contato) { this.contato.set(contato); }

    public boolean isAtivo() { return ativo.get(); }
    public BooleanProperty ativoProperty() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo.set(ativo); }
}