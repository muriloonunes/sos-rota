package mhd.sosrota.service;

import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.GrafoCidade;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;
import mhd.sosrota.presentation.OpcaoDespacho;
import mhd.sosrota.repository.AmbulanciaRepository;
import mhd.sosrota.repository.AtendimentoRepository;
import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.enums.StatusOcorrencia;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 04/12/2025
 * @brief Class AtendimentoService
 */
public class AtendimentoService {
    private final GrafoCidadeService grafoService;
    private final AmbulanciaRepository ambulanciaRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final CicloAtendimentoService cicloAtendimentoService;

    private static final double VELOCIDADE = 60.0;

    public AtendimentoService(GrafoCidadeService grafoService, AmbulanciaRepository ambulanciaRepository, AtendimentoRepository atendimentoRepository, CicloAtendimentoService cicloAtendimentoService) {
        this.grafoService = grafoService;
        this.ambulanciaRepository = ambulanciaRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.cicloAtendimentoService = cicloAtendimentoService;
    }

    public List<OpcaoDespacho> buscarOpcoesDeDespacho(Ocorrencia ocorrencia) {
        GrafoCidade grafo = grafoService.obterGrafo();
        Bairro localOcorrencia = ocorrencia.getBairro();

        // dijkstra retorna um mapa: Bairro -> Distância em KM até a ocorrência
        Map<Bairro, Double> mapaDistancias = grafo.calcularDistanciasParaTodos(localOcorrencia);

        List<Ambulancia> disponiveis = ambulanciaRepository.obterAmbulanciaStatus(StatusAmbulancia.DISPONIVEL);

        List<OpcaoDespacho> opcoes = new ArrayList<>();

        for (Ambulancia amb : disponiveis) {
            if (ocorrencia.getGravidadeOcorrencia() == GravidadeOcorrencia.ALTA
                    && amb.getTipoAmbulancia() != TipoAmbulancia.UTI) {
                continue; // Ignora esta ambulância
            }
            Bairro localAmbulancia = amb.getBairroBase();

            // Verifica se o Dijkstra encontrou caminho até onde a ambulância está
            if (mapaDistancias.containsKey(localAmbulancia)) {
                double distKm = mapaDistancias.get(localAmbulancia);

                if (distKm != Double.POSITIVE_INFINITY) {
                    // Calcula tempo (t = d / v) * 60 min
                    double tempoMin = (distKm / VELOCIDADE) * 60;

                    opcoes.add(new OpcaoDespacho(amb, distKm, tempoMin));
                }
            }
        }
        opcoes.sort(Comparator.comparingDouble(OpcaoDespacho::getTempoEstimadoMin));

        return opcoes;
    }

    public void realizarDespacho(Ocorrencia ocorrencia, Ambulancia ambulancia, double distanciaKm, double tempoEstimado) {
        if (ocorrencia == null || ambulancia == null) {
            throw new IllegalArgumentException("Ocorrência e ambulância não podem ser nulos.");
        }

        if (ocorrencia.getStatusOcorrencia() != StatusOcorrencia.ABERTA) {
            throw new IllegalStateException("Só é possível despachar ocorrências abertas.");
        }

        if (ambulancia.getStatusAmbulancia() != StatusAmbulancia.DISPONIVEL) {
            throw new IllegalStateException("Só é possível despachar ambulâncias disponíveis.");
        }

        ocorrencia.setStatusOcorrencia(StatusOcorrencia.DESPACHADA);
        ambulancia.setStatusAmbulancia(StatusAmbulancia.EM_ATENDIMENTO);

        Atendimento atendimento = new Atendimento(ocorrencia, ambulancia, distanciaKm);

        atendimentoRepository.salvar(atendimento, ocorrencia, ambulancia);
        cicloAtendimentoService.iniciarCicloAtendimento(atendimento.getId(),  tempoEstimado);
    }
}
