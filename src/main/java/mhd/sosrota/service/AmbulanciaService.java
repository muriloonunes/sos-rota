package mhd.sosrota.service;

import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.repository.AmbulanciaRepository;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 28/11/2025
 * @brief Class AmbulanciaService
 */
public class AmbulanciaService {
    private final AmbulanciaRepository repo;

    public AmbulanciaService(AmbulanciaRepository repo) {
        this.repo = repo;
    }

    public List<Ambulancia> listarTodasAmbulancias() {
        return repo.listarTodasAmbulancias();
    }

    public List<Ambulancia> listarAmbulanciaSemEquipe() {
        return repo.listarAmbulanciaSemEquipe();
    }

    public Ambulancia encontrarPorId(long id) {
        return repo.encontrarPorId(id);
    }

    public Ambulancia encontrarPorIdComBairro(long id) {
        return repo.encontrarPorIdComBairro(id);
    }

    public void cadastrarAmbulancia(String placa, String tipoDesc, Bairro base) {
        if (!placa.matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
            throw new CadastroException("A placa deve estar no formato ABC1D23.");
        }
        try {
            StatusAmbulancia status = StatusAmbulancia.INATIVA; //sempre iniciamos a ambulancia como inativa, pois ela se inicia sem equipe
            TipoAmbulancia tipo = TipoAmbulancia.fromDescricao(tipoDesc);
            Ambulancia ambulancia = new Ambulancia(status, tipo, placa, base);
            repo.salvar(ambulancia);
        } catch (Exception e) {
            Throwable causaAtual = e;
            while (causaAtual != null) {
                if (causaAtual instanceof ConstraintViolationException) {
                    throw new CadastroException("Já existe uma ambulância cadastrada com essa placa.");
                }
                if (causaAtual instanceof SQLException) {
                    if ("23505".equals(((SQLException) causaAtual).getSQLState())) {
                        throw new CadastroException("Já existe uma ambulância cadastrada com essa placa.");
                    }
                }
                causaAtual = causaAtual.getCause();
            }

            throw e;
        }
    }

    public void atualizarAmbulancia(String placa, String statusDesc, String tipoDesc, Bairro base, long id) {
        if (!placa.matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
            throw new CadastroException("A placa deve estar no formato ABC1D23.");
        }
        if (base == null) {
            throw new CadastroException("Selecione uma base válida.");
        }
        try {
            Ambulancia ambulanciaExistente = repo.encontrarPorId(id);
            if (ambulanciaExistente == null) {
                throw new CadastroException("Ambulância não encontrada.");
            }
            if (ambulanciaExistente.getStatusAmbulancia() == StatusAmbulancia.EM_ATENDIMENTO) {
                throw new CadastroException("Não é possível atualizar uma ambulância que está em atendimento.");
            }

            StatusAmbulancia status = StatusAmbulancia.fromDescricao(statusDesc);
            TipoAmbulancia tipo = TipoAmbulancia.fromDescricao(tipoDesc);
            Ambulancia ambulancia = new Ambulancia(status, tipo, placa, base);
            ambulancia.setId(id);
            repo.atualizarAmbulancia(ambulancia);
        } catch (Exception e) {
            Throwable causaAtual = e;
            while (causaAtual != null) {
                if (causaAtual instanceof ConstraintViolationException) {
                    throw new CadastroException("Já existe uma ambulância cadastrada com essa placa.");
                }
                if (causaAtual instanceof SQLException) {
                    if ("23505".equals(((SQLException) causaAtual).getSQLState())) {
                        throw new CadastroException("Já existe uma ambulância cadastrada com essa placa.");
                    }
                }
                causaAtual = causaAtual.getCause();
            }
            throw e;
        }
    }

    public boolean deletarAmbulancia(long id) {
        //TODO ver quais regras de negocio impedem uma ambulancia de ser deletada
        return repo.deletarAmbulancia(id);
    }

    public long obterQtdAmbulanciaStatus(StatusAmbulancia status) {
        return repo.obterAmbulanciaStatus(status).size();
    }
    public List<Ambulancia> obterAmbulanciaStatus(StatusAmbulancia status) {
        return repo.obterAmbulanciaStatus(status);
    }
}
