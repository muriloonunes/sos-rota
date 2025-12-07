package mhd.sosrota.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.StatusOcorrencia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 06/12/2025
 * @brief Class CicloAtendimentoService
 */
public class CicloAtendimentoService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final EntityManagerFactory emf;

    private final long TEMPO_ATENDIMENTO = 7; // Tempo fixo que a equipe leva pra atender
    private final long FATOR_CONVERSAO = 10;

    public CicloAtendimentoService(EntityManagerFactory emf) {
        this.emf = emf;
        verificarPendencias();
    }

    public void iniciarCicloAtendimento(Long atendimentoId, double tempoViagemMinutos) {
        long tempoSimulado = (long) (tempoViagemMinutos * FATOR_CONVERSAO); //pra simular, 1 min da simulação = 10 segundos reais
        System.out.println(">> SIMULAÇÃO INICIADA para Atendimento " + atendimentoId);

        //simula a chegada da ambulancia no local após o tempo da viagem
        scheduler.schedule(() -> registrarChegada(atendimentoId), tempoSimulado, TimeUnit.SECONDS);

        //smiula o fim do atendimento e o início da volta da ambulancia
        scheduler.schedule(() -> registrarConclusaoAtendimento(atendimentoId), tempoSimulado + TEMPO_ATENDIMENTO, TimeUnit.SECONDS);

        //voltou pra base após o tempo da viagem de ida + tempo de atendimento + viagem de volta
        scheduler.schedule(() -> registrarRetornoBase(atendimentoId), (tempoSimulado * 2) + TEMPO_ATENDIMENTO, TimeUnit.SECONDS);
    }

    private void verificarPendencias() {
        rodaTransacao(em -> {
            List<Atendimento> pendentes = em.createQuery(
                    "SELECT a FROM Atendimento a WHERE a.dataHoraDespacho IS NOT NULL AND a.dataHoraConclusao IS NULL",
                    Atendimento.class
            ).getResultList();

            for (Atendimento at : pendentes) {
                tratarAtendimentoPendente(at, em);
            }
        });
    }

    private void tratarAtendimentoPendente(Atendimento at, EntityManager em) {
        LocalDateTime dataDespacho = at.getDataHoraDespacho();
        LocalDateTime agora = LocalDateTime.now();

        long tempoViagem = (long) (at.getDistanciaKm() * FATOR_CONVERSAO);  //o tempo entre sair da base e chegar na ocorrencia

        LocalDateTime horaChegada = dataDespacho.plusSeconds(tempoViagem); //hora de chegar na ocorrencia
        LocalDateTime horaFimAtendimento = horaChegada.plusSeconds(TEMPO_ATENDIMENTO); //hora de terminar o atendimento
        LocalDateTime horaRetornoBase = horaFimAtendimento.plusSeconds(tempoViagem); //hora de voltar pra base

        if (agora.isAfter(horaRetornoBase)) {
            at.getOcorrencia().setStatusOcorrencia(StatusOcorrencia.CONCLUIDA);
            at.getAmbulancia().setStatusAmbulancia(StatusAmbulancia.DISPONIVEL);

            at.setDataHoraChegada(horaChegada); // chegou na ocorrencia
            at.setDataHoraConclusao(horaFimAtendimento);
            em.merge(at.getOcorrencia());
            em.merge(at.getAmbulancia());
            em.merge(at);
        } else {
            long delayChegada = java.time.Duration.between(agora, horaChegada).getSeconds();
            long delayFimAtendimento = java.time.Duration.between(agora, horaFimAtendimento).getSeconds();
            long delayRetorno = java.time.Duration.between(agora, horaRetornoBase).getSeconds();

            // Se ainda não chegou na ocorrência, agendar chegada
            if (delayChegada > 0) {
                scheduler.schedule(() -> registrarChegada(at.getId()), delayChegada, TimeUnit.SECONDS);
            }

            // Se ainda não acabou o atendimento, agendar fim
            if (delayFimAtendimento > 0) {
                scheduler.schedule(() -> registrarConclusaoAtendimento(at.getId()), delayFimAtendimento, TimeUnit.SECONDS);
            }

            // O retorno sempre será agendado
            if (delayRetorno > 0) {
                scheduler.schedule(() -> registrarRetornoBase(at.getId()), delayRetorno, TimeUnit.SECONDS);
            }
        }
    }

    private void registrarChegada(Long atendimentoId) {
        rodaTransacao(em -> {
            Atendimento at = em.find(Atendimento.class, atendimentoId);
            if (at != null) {
                at.setDataHoraChegada(LocalDateTime.now());
                at.getOcorrencia().setStatusOcorrencia(StatusOcorrencia.EM_ATENDIMENTO);
                System.out.println(">>> [Simulação] Ambulância chegou na ocorrência " + at.getOcorrencia().getId());
            }
        });
    }

    private void registrarConclusaoAtendimento(Long atendimentoId) {
        rodaTransacao(em -> {
            Atendimento at = em.find(Atendimento.class, atendimentoId);
            if (at != null) {
                at.setDataHoraConclusao(LocalDateTime.now());
                at.getOcorrencia().setStatusOcorrencia(StatusOcorrencia.CONCLUIDA);

                System.out.println(">>> [Simulação] Atendimento concluído. Ambulância voltando.");
            }
        });
    }

    private void registrarRetornoBase(Long atendimentoId) {
        rodaTransacao(em -> {
            Atendimento at = em.find(Atendimento.class, atendimentoId);
            if (at != null) {
                Ambulancia amb = at.getAmbulancia();
                amb.setStatusAmbulancia(StatusAmbulancia.DISPONIVEL);
                System.out.println(">>> [Simulação] Ambulância " + amb.getPlaca() + " disponível na base.");
            }
        });
    }

    // Helper para gerenciar transações em threads isoladas
    private void rodaTransacao(Consumer<EntityManager> acao) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            acao.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public void pararServico() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
