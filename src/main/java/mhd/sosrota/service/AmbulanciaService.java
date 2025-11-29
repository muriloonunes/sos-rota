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

    public void cadastrarAmbulancia(String placa, String statusDesc, String tipoDesc, Bairro base) {
        if (!placa.matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
            throw new CadastroException("A placa deve estar no formato ABC1D23.");
        }
        try {
            StatusAmbulancia status = StatusAmbulancia.fromDescricao(statusDesc);
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

    public Ambulancia atualizarAmbulancia(String placa, String statusDesc, String tipoDesc, Bairro base, long id) {
        if (!placa.matches("^[A-Z]{3}[0-9][A-Z][0-9]{2}$")) {
            throw new CadastroException("A placa deve estar no formato ABC1D23.");
        }
        if (base == null) {
            throw new CadastroException("Selecione uma base válida.");
        }
        try {
            StatusAmbulancia status = StatusAmbulancia.fromDescricao(statusDesc);
            TipoAmbulancia tipo = TipoAmbulancia.fromDescricao(tipoDesc);
            Ambulancia ambulancia = new Ambulancia(status, tipo, placa, base);
            ambulancia.setId(id);
            return repo.atualizarAmbulancia(ambulancia);
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
}
