package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.enums.StatusAmbulancia;

import java.util.List;

/**
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 27/11/2025
 * @brief class AmbulanciaRepositoryImpl
 */

public class AmbulanciaRepositoryImpl implements AmbulanciaRepository {
    @Override
    public Ambulancia encontrarPorId(long id) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.find(Ambulancia.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Ambulancia encontrarPorIdComBairro(long id) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Ambulancia a " +
                                    "LEFT JOIN FETCH a.bairroBase " +
                                    "WHERE a.id = :id", Ambulancia.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
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
    public List<Ambulancia> listarAmbulanciaSemEquipe() {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery(
                    "SELECT a FROM Ambulancia a " +
                            "LEFT JOIN FETCH a.bairroBase " +
                            "WHERE a.id NOT IN (" +
                            "   SELECT e.ambulancia.id FROM Equipe e WHERE e.ativo = true" +
                            ")",
                    Ambulancia.class
            ).getResultList();
        } catch (NoResultException e) {
            return List.of();
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
    public void atualizarAmbulancia(Ambulancia dados) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();

            Ambulancia original = em.find(Ambulancia.class, dados.getId());
            if (original == null) throw new IllegalArgumentException("Houve um erro ao editar a ambulância");

            original.setPlaca(dados.getPlaca());
            original.setTipoAmbulancia(dados.getTipoAmbulancia());
            original.setStatusAmbulancia(dados.getStatusAmbulancia());
            original.setBairroBase(dados.getBairroBase());
            em.getTransaction().commit();
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

    @Override
    public long obterAmbulanciaStatus(StatusAmbulancia status) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery("SELECT COUNT(a) FROM Ambulancia a WHERE a.statusAmbulancia = :status", Long.class)
                    .setParameter("status", status)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
