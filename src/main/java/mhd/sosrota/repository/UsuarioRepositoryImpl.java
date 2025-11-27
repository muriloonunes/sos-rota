package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Usuario;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
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
    public boolean salvar(Usuario usuario) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
            return true;
        }
    }
}
