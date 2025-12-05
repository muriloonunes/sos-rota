package mhd.sosrota.service;


import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.repository.ProfissionalRepository;

import java.util.List;

/**
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 29/11/2025
 * @brief Class AmbulanciaService
 */
public record ProfissionalService(ProfissionalRepository repo) {

    public void cadastrarProfissional(String nome, String funcaoDesc, String email) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new CadastroException("O nome do profissional é obrigatório.");
        }
        if (nome.length() > 100) {
            throw new CadastroException("O nome não pode ter mais de 100 caracteres.");
        }
        if (funcaoDesc == null || funcaoDesc.trim().isEmpty()) {
            throw new CadastroException("A função é obrigatória.");
        }
        if (email != null && !email.trim().isEmpty() && email.length() > 50) {
            throw new CadastroException("O email não pode ter mais de 50 caracteres.");
        }
        if (!email.contains("@") && !email.trim().isEmpty()) {
            throw new CadastroException("Digite um email válido.");
        }

        try {
            FuncaoProfissional funcao = FuncaoProfissional.fromNome(funcaoDesc);
            if (funcao == null) {
                throw new CadastroException("Função inválida.");
            }

            Profissional profissionalExistente = repo.buscarPorNome(nome);
            if (profissionalExistente != null) {
                throw new CadastroException("Já existe um profissional cadastrado com esse nome.");
            }

            Profissional profissional = new Profissional();
            profissional.setNome(nome);
            profissional.setFuncaoProfissional(funcao);
            profissional.setContato(email);
            profissional.setAtivo(true);

            if (!repo.salvar(profissional)) {
                throw new CadastroException("Erro ao salvar profissional no banco de dados.");
            }
        } catch (CadastroException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CadastroException("Erro ao cadastrar profissional.");
        }
    }

    public List<Profissional> listarTodosProfissionais() {
        return repo.listarTodos();
    }

    public List<Profissional> listarPorFuncao(FuncaoProfissional funcao) {
        return repo.listarPorFuncao(funcao);
    }

    public List<Profissional> listarProfissionaisDisponiveis() {
        return repo.listarDisponiveis();
    }

    public void atualizarProfissional(Long id, String nome, String funcaoDesc, String email) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new CadastroException("O nome do profissional é obrigatório.");
        }
        if (funcaoDesc == null || funcaoDesc.trim().isEmpty()) {
            throw new CadastroException("A função é obrigatória.");
        }

        try {
            Profissional profissionalExistente = repo.buscarPorId(id);

            if (profissionalExistente == null) {
                throw new CadastroException("Profissional não encontrado.");
            }

            FuncaoProfissional novaFuncao = FuncaoProfissional.fromNome(funcaoDesc);
            if (novaFuncao == null) {
                throw new CadastroException("Função inválida.");
            }

            boolean mudouDeFuncao = !profissionalExistente.getFuncaoProfissional().equals(novaFuncao);

            if (mudouDeFuncao && profissionalExistente.getEquipe() != null && profissionalExistente.getEquipe().isAtivo()) {
                throw new CadastroException("Não é possível alterar a função de um profissional que está alocado em uma equipe ativa.\nRemova-o da equipe ou a deixe inativa");
            }

            profissionalExistente.setId(id);
            profissionalExistente.setNome(nome);
            profissionalExistente.setFuncaoProfissional(novaFuncao);
            profissionalExistente.setContato(email);

            repo.atualizar(profissionalExistente);
        } catch (CadastroException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CadastroException("Erro ao atualizar profissional.");
        }
    }

    public boolean deletarProfissional(Long id) {
        //todo avaliar se essas verificações são suficientes
        Profissional profissional = repo.buscarPorId(id);
        // cada profissional tem botao proprio, ent meio q o id sempre vai ser valido, mas quero garantir, vai que
        if (profissional == null) {
            throw new CadastroException("Profissional não encontrado para exclusão.");
        }

        Equipe equipe = profissional.getEquipe();
        if (equipe != null && equipe.isAtivo()) {
            throw new CadastroException(
                    "O profissional está alocado em uma equipe ativa.\n" +
                            "Remova-o da equipe ou inative a equipe antes de excluir."
            );
        }

        return repo.deletar(profissional);
    }
}
