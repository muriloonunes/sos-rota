package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;

import java.util.Collections;
import java.util.List;

/**
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 27/11/2025
 * @brief class ProfissionalRepositoryImpl
 */

public class ProfissionalRepositoryImpl implements ProfissionalRepository {

    @Override
    public boolean salvar(Profissional profissional) {
        EntityManager em = JpaManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(profissional);
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
    public Profissional buscarPorId(Long id) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM Profissional p LEFT JOIN FETCH p.equipe WHERE p.id = :id", Profissional.class
                    ).setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Profissional buscarPorNome(String nome) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            TypedQuery<Profissional> q = em.createQuery(
                    "SELECT p FROM Profissional p WHERE p.nome = :nome", Profissional.class);
            q.setParameter("nome", nome);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Profissional> listarTodos() {
        try (EntityManager em = JpaManager.getEntityManager()) {
            TypedQuery<Profissional> q = em.createQuery(
                    "SELECT p FROM Profissional p ORDER BY p.id", Profissional.class);
            return q.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Profissional> listarPorFuncao(FuncaoProfissional funcao) {
        if (funcao == null) return Collections.emptyList();
        try (EntityManager em = JpaManager.getEntityManager()) {
            TypedQuery<Profissional> q = em.createQuery(
                    "SELECT p FROM Profissional p WHERE p.funcaoProfissional = :funcao ORDER BY p.nome",
                    Profissional.class);
            q.setParameter("funcao", funcao);
            return q.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Profissional> listarDisponiveis() {
        try (EntityManager em = JpaManager.getEntityManager()) {
            TypedQuery<Profissional> q = em.createQuery(
                    "SELECT p FROM Profissional p WHERE p.ativo = true AND p.equipe IS NULL",
                    Profissional.class);
            return q.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Profissional atualizar(Profissional profissional) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            Profissional atualizado = em.merge(profissional);
            em.getTransaction().commit();
            return atualizado;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deletar(Profissional profissional) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            Profissional profissionalDeletar = em.find(Profissional.class, profissional.getId());
            if (profissionalDeletar == null) {
                return false;
            }
            em.remove(profissionalDeletar);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
