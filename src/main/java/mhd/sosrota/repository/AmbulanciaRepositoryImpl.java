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
            return em.createQuery("SELECT a FROM Ambulancia a JOIN FETCH a.bairroBase", Ambulancia.class)
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
    public Ambulancia atualizarAmbulancia(Ambulancia ambulancia) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            Ambulancia atualizado = em.merge(ambulancia);
            em.getTransaction().commit();
            return atualizado;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deletarAmbulancia(Ambulancia ambulancia) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            Ambulancia ambulanciaDeletar = em.find(Ambulancia.class, ambulancia.getPlaca());
            if (ambulanciaDeletar == null) {
                return false;
            }
            em.remove(ambulanciaDeletar);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
