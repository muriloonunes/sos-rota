package mhd.sosrota.infrastructure.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 26/11/2025
 * @brief Class JpaManager
 */
public class JpaManager {
    private static EntityManagerFactory emf;

    public static void initialize() {
        if (emf == null) {
            try {
                emf = Persistence.createEntityManagerFactory("SosRota");
            } catch (Exception e) {
                throw new RuntimeException("Erro ao conectar ao banco de dados.", e);
            }
        }
    }

    public static EntityManagerFactory getFactory() {
        if (emf == null) {
            throw new IllegalStateException("O EntityManagerFactory não foi inicializado. O banco está offline?");
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("Modo Offline: Não é possível criar EntityManager.");
        }
        return emf.createEntityManager();
    }

    public static boolean isOffline() {
        return emf == null || !emf.isOpen();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
