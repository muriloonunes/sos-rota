package mhd.sosrota.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.GravidadeOcorrencia;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

    @Override
    public Atendimento buscarPorOcorrenciaId(Long ocorrenciaId) {
        try (EntityManager em = JpaManager.getEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Atendimento a " +
                                    "JOIN FETCH a.ambulancia " +
                                    "WHERE a.ocorrencia.id = :ocid", Atendimento.class)
                    .setParameter("ocid", ocorrenciaId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Atendimento> consultarHistorico(Long ambulanciaId, LocalDateTime inicio, LocalDateTime fim, GravidadeOcorrencia gravidade) {
        try (EntityManager em = JpaManager.getEntityManager()) {

            StringBuilder jpql = new StringBuilder(
                    "SELECT a FROM Atendimento a WHERE 1=1"
            );

            if (ambulanciaId != null) {
                jpql.append(" AND a.ambulancia.id = :amb");
            }
            if (inicio != null) {
                jpql.append(" AND a.dataHoraDespacho >= :inicio");
            }
            if (fim != null) {
                jpql.append(" AND a.dataHoraDespacho <= :fim");
            }
            if (gravidade != null) {
                jpql.append(" AND a.ocorrencia.gravidadeOcorrencia = :grav");
            }

            TypedQuery<Atendimento> query = em.createQuery(jpql.toString(), Atendimento.class);

            if (ambulanciaId != null) query.setParameter("amb", ambulanciaId);
            if (inicio != null) query.setParameter("inicio", inicio);
            if (fim != null) query.setParameter("fim", fim);
            if (gravidade != null) query.setParameter("grav", gravidade);

            return query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Double tempoMedioRespostaPorGravidade(GravidadeOcorrencia gravidade) {
        try (EntityManager em = JpaManager.getEntityManager()) {

            String jpql = """
                        SELECT AVG(
                            epoch(a.dataHoraChegada) - epoch(a.dataHoraDespacho)
                        )
                        FROM Atendimento a
                        WHERE a.ocorrencia.gravidadeOcorrencia = :grav
                          AND a.dataHoraChegada IS NOT NULL
                    """;


            TypedQuery<Double> q = em.createQuery(jpql, Double.class);
            q.setParameter("grav", gravidade);

            return q.getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<Object[]> mapaOcorrenciasPorBairro() {
        try (EntityManager em = JpaManager.getEntityManager()) {

            String jpql =
                    "SELECT a.ocorrencia.bairro.nome, COUNT(a.id) " +
                            "FROM Atendimento a " +
                            "GROUP BY a.ocorrencia.bairro.nome " +
                            "ORDER BY COUNT(a.id) DESC";

            return em.createQuery(jpql, Object[].class)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
