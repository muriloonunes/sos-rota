package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Ambulancia;

import java.util.List;

/**
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 27/11/2025
 * @brief class AmbulanciaRepositoryImpl
 */

public class AmbulanciaRepositoryImpl implements AmbulanciaRepository {
    @Override
    public Ambulancia encontrarPorPlaca(String placa) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Ambulancia a WHERE a.placa = :placa", Ambulancia.class)
                    .setParameter("placa", placa)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Ambulancia> listarTodasAmbulancias() {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery("SELECT a FROM Ambulancia a JOIN FETCH a.bairroBase ORDER BY a.id", Ambulancia.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void salvar(Ambulancia ambulancia) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(ambulancia);
            em.getTransaction().commit();
        }
    }

    @Override
    public Ambulancia atualizarAmbulancia(Ambulancia dados) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();

            Ambulancia original = em.find(Ambulancia.class, dados.getId());
            if (original == null) throw new IllegalArgumentException("Houve um erro ao editar a ambulância");

            original.setPlaca(dados.getPlaca());
            original.setTipoAmbulancia(dados.getTipoAmbulancia());
            original.setStatusAmbulancia(dados.getStatusAmbulancia());
            original.setBairroBase(dados.getBairroBase());
            em.getTransaction().commit();

            return original;
        }
    }

    @Override
    public boolean deletarAmbulancia(long id) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            Ambulancia ambulanciaDeletar = em.find(Ambulancia.class, id);
            if (ambulanciaDeletar == null) {
                return false;
            }
            em.remove(ambulanciaDeletar);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
