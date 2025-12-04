package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.Ocorrencia;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 04/12/2025
 * @brief Class AtendimentoRepositoryImpl
 */
public class AtendimentoRepositoryImpl implements AtendimentoRepository {
    @Override
    public Atendimento salvar(Atendimento atendimento, Ocorrencia ocorrencia, Ambulancia ambulancia) {
        EntityManager em = JpaManager.getEntityManager();
        try {
            em.getTransaction().begin();

            em.merge(ocorrencia);
            em.merge(ambulancia);

            em.persist(atendimento);
            em.getTransaction().commit();

            em.refresh(atendimento);
            return atendimento;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return null;
        } finally {
            em.close();
        }
    }
}
