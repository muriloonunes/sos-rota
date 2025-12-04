package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.StatusAmbulancia;

import java.util.Collections;
import java.util.List;

public class EquipeRepositoryImpl implements EquipeRepository {

    @Override
    public Boolean insertEquipe(Equipe equipe) {
        EntityManager em = JpaManager.getEntityManager();
        try {
            em.getTransaction().begin();

            Ambulancia amb = equipe.getAmbulancia();
            amb.setStatusAmbulancia(StatusAmbulancia.DISPONIVEL);
            em.merge(amb);

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
        } finally {
            em.close();
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
        EntityManager em = JpaManager.getEntityManager();
        try {
            em.getTransaction().begin();

            Equipe equipeAntiga = em.find(Equipe.class, equipeAlterada.getId());
            Ambulancia ambAntiga = equipeAntiga.getAmbulancia();
            Ambulancia ambNova = em.find(Ambulancia.class, equipeAlterada.getAmbulancia().getId());

            if (!ambAntiga.getId().equals(ambNova.getId())) {
                if (ambAntiga.getStatusAmbulancia() != StatusAmbulancia.DESATIVADA) {
                    ambAntiga.setStatusAmbulancia(StatusAmbulancia.INATIVA);
                    em.merge(ambAntiga);
                }
            }

            if (equipeAlterada.isAtivo()) {
                if (ambNova.getStatusAmbulancia() != StatusAmbulancia.EM_ATENDIMENTO &&
                        ambNova.getStatusAmbulancia() != StatusAmbulancia.MANUTENCAO) {

                    ambNova.setStatusAmbulancia(StatusAmbulancia.DISPONIVEL);
                }
            } else {
                if (ambNova.getStatusAmbulancia() != StatusAmbulancia.DESATIVADA) {
                    ambNova.setStatusAmbulancia(StatusAmbulancia.INATIVA);
                }
            }
            em.merge(ambNova);


            em.createQuery("UPDATE Profissional p SET p.equipe = NULL WHERE p.equipe.id = :id")
                    .setParameter("id", equipeAlterada.getId())
                    .executeUpdate();

            Equipe equipeManaged = em.merge(equipeAlterada);

            for (Profissional p : equipeAlterada.getProfissionais()) {
                Profissional pManaged = em.find(Profissional.class, p.getId());
                pManaged.setEquipe(equipeManaged);
                em.merge(pManaged);
            }

            em.getTransaction().commit();
            return true;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
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

            Ambulancia amb = equipeBD.getAmbulancia();
            if (amb != null) {
                if (amb.getStatusAmbulancia() != StatusAmbulancia.DESATIVADA) {
                    amb.setStatusAmbulancia(StatusAmbulancia.INATIVA);
                    em.merge(amb);
                }
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

    @Override
    public boolean existeEquipeComAmbulancia(long idAmbulancia, long idEquipeIgnorada) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Equipe e " +
                                    "WHERE e.ambulancia.id = :ambId " +
                                    "AND e.ativo = true " +
                                    "AND e.id <> :equipeId",
                            Long.class)
                    .setParameter("ambId", idAmbulancia)
                    .setParameter("equipeId", idEquipeIgnorada)
                    .getSingleResult();

            return count > 0;
        }
    }
}
