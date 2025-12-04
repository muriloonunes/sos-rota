package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.StatusOcorrencia;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class OcorrenciaRepositoryImpl implements OcorrenciaRepository {

    @Override
    public Boolean salvar(Ocorrencia ocorrencia) {
        EntityManager em = JpaManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ocorrencia);
            em.getTransaction().commit();
            return true;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;

        } finally {
            em.close();
        }
    }

    @Override
    public Ocorrencia buscarPorId(Long id) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.find(Ocorrencia.class, id);
        }
    }

    @Override
    public List<Ocorrencia> buscarPorStatus(String status) {
        try (EntityManager em = JpaManager.getEntityManager()) {

            StatusOcorrencia statusEnum = StatusOcorrencia.valueOf(status.toUpperCase());

            TypedQuery<Ocorrencia> q = em.createQuery(
                    "SELECT o FROM Ocorrencia o " +
                            "JOIN FETCH o.bairro " +
                            "WHERE o.statusOcorrencia = :status",
                    Ocorrencia.class
            );

            q.setParameter("status", statusEnum);

            return q.getResultList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Ocorrencia> buscarPorTipo(String tipo) {
        try (EntityManager em = JpaManager.getEntityManager()) {

            TypedQuery<Ocorrencia> q = em.createQuery(
                    "SELECT o FROM Ocorrencia o " +
                            "JOIN FETCH o.bairro " +
                            "WHERE LOWER(o.tipoOcorrencia) LIKE LOWER(:tipo)",
                    Ocorrencia.class
            );

            q.setParameter("tipo", "%" + tipo + "%");

            return q.getResultList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Ocorrencia> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        try (EntityManager em = JpaManager.getEntityManager()) {

            TypedQuery<Ocorrencia> q = em.createQuery(
                    "SELECT o FROM Ocorrencia o " +
                            "JOIN FETCH o.bairro " +
                            "WHERE o.dataHoraAbertura BETWEEN :inicio AND :fim",
                    Ocorrencia.class
            );

            q.setParameter("inicio", inicio);
            q.setParameter("fim", fim);

            return q.getResultList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Ocorrencia> listarTodas() {
        try (EntityManager em = JpaManager.getEntityManager()) {

            TypedQuery<Ocorrencia> q = em.createQuery(
                    "SELECT o FROM Ocorrencia o " +
                            "JOIN FETCH o.bairro " +
                            "ORDER BY o.dataHoraAbertura DESC",
                    Ocorrencia.class
            );

            return q.getResultList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Boolean atualizar(Ocorrencia ocorrencia) {
        EntityManager em = JpaManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(ocorrencia);
            em.getTransaction().commit();
            return true;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;

        } finally {
            em.close();
        }
    }

    @Override
    public Boolean deletar(Long id) {
        EntityManager em = JpaManager.getEntityManager();

        try {
            Ocorrencia o = em.find(Ocorrencia.class, id);
            if (o == null) return false;

            em.getTransaction().begin();
            em.remove(o);
            em.getTransaction().commit();
            return true;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;

        } finally {
            em.close();
        }
    }
}
