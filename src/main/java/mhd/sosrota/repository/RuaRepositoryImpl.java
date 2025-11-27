package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Rua;

import java.util.List;

public class RuaRepositoryImpl implements RuaRepository {
    @Override
    public List<Rua> obterRuas() {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery("""
                        SELECT DISTINCT r
                        FROM Rua r
                        JOIN FETCH r.origem
                        JOIN FETCH r.destino
                    """, Rua.class).getResultList();
        }
    }
}
