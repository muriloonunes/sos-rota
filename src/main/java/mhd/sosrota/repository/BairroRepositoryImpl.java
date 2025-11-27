package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Bairro;

import java.util.List;

public class BairroRepositoryImpl implements BairroRepository {
    @Override
    public List<Bairro> obterBairros() {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery("SELECT b FROM Bairro b", Bairro.class).getResultList();
        }
    }

    @Override
    public Bairro encontrarPorId(long id) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.find(Bairro.class, id);
        }
    }
}
