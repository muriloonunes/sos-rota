package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Equipe;

import java.util.Collections;
import java.util.List;

public class EquipeRepositoryImpl implements EquipeRepository {

    @Override
    public Boolean insertEquipe(Equipe equipe) {
        EntityManager em = JpaManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(equipe);
            em.getTransaction().commit();
            return (Boolean) true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return (Boolean) false;
        }
    }

    @Override
    public Equipe buscaPorPlacaAmbulancia(String placa) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            TypedQuery<Equipe> q = em.createQuery(
                    "SELECT e FROM Equipe e " +
                            "JOIN FETCH e.ambulancia a " +
                            "LEFT JOIN FETCH e.profissionais " +
                            "WHERE a.placa = :placa",
                    Equipe.class
            );
            q.setParameter("placa", placa);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Equipe> buscaPorNomeProfissional(String nome) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            TypedQuery<Equipe> q = em.createQuery(
                    "SELECT DISTINCT e FROM Equipe e " +
                            "JOIN FETCH e.profissionais p " +
                            "LEFT JOIN FETCH e.ambulancia " +
                            "WHERE LOWER(p.nome) LIKE LOWER(:nome)",
                    Equipe.class
            );
            q.setParameter("nome", "%" + nome + "%");
            return q.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Equipe> findAllEquipes() {
        try (EntityManager em = JpaManager.getEntityManager()) {
            TypedQuery<Equipe> q = em.createQuery(
                    "SELECT DISTINCT e FROM Equipe e " +
                            "LEFT JOIN FETCH e.ambulancia " +
                            "LEFT JOIN FETCH e.profissionais " +
                            "ORDER BY e.id",
                    Equipe.class
            );
            return q.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Boolean updateEquipe(Equipe equipe) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            em.merge(equipe);
            em.getTransaction().commit();
            return (Boolean) true;
        } catch (RuntimeException e) {
            return (Boolean) false;
        }
    }

    @Override
    public Boolean deleteEquipe(long id) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();

            Equipe equipeBD = em.find(Equipe.class, id);
            if (equipeBD == null) {
                return false;
            }

            em.remove(equipeBD);
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
