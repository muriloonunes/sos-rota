package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Profissional;

import java.util.Collections;
import java.util.List;

public class EquipeRepositoryImpl implements EquipeRepository {

    @Override
    public Boolean insertEquipe(Equipe equipe) {
        EntityManager em = JpaManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(equipe);
            for (Profissional p : equipe.getProfissionais()) {
                em.merge(p);
            }
            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
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
        }
    }

    @Override
    public Boolean updateEquipe(Equipe equipeAlterada) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();

            em.createQuery("UPDATE Profissional p SET p.equipe = NULL WHERE p.equipe.id = :id")
                    .setParameter("id", equipeAlterada.getId())
                    .executeUpdate();

            Equipe equipe = em.merge(equipeAlterada);

            for (Profissional p  : equipeAlterada.getProfissionais()) {
                p.setEquipe(equipe);
                em.merge(p);
            }

            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            return false;
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

            for (Profissional p : equipeBD.getProfissionais()) {
                p.setEquipe(null);
            }

            equipeBD.getProfissionais().clear();

            em.remove(equipeBD);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
