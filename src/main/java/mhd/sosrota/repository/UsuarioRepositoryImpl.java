package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Usuario;

import java.util.List;

/**
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @author Hartur Sales <hartursalesxavier@gmail.com>
 * @date 25/11/2025
 * @brief Class UsuarioRepository
 */
public class UsuarioRepositoryImpl implements UsuarioRepository {
    @Override
    public Usuario encontrarPorUsername(String username) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery(
                            "SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Usuario> listarTodosUsuarios() {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void salvar(Usuario usuario) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        }
    }

    @Override
    public Usuario atualizarUsuario(Usuario usuario) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            Usuario atualizado = em.merge(usuario);
            em.getTransaction().commit();
            return atualizado;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deletarUsuario(Usuario usuario) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            Usuario usuarioDeletar = em.find(Usuario.class, usuario.getUsername());
            if (usuarioDeletar == null) {
                return false;
            }

            em.remove(usuarioDeletar);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
