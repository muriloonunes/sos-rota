package mhd.sosrota.service;

import mhd.sosrota.model.Atendimento;
import mhd.sosrota.model.enums.GravidadeOcorrencia;
import mhd.sosrota.repository.AtendimentoRepository;
import mhd.sosrota.repository.AtendimentoRepositoryImpl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {

    private final AtendimentoRepository atendimentoRepository = new AtendimentoRepositoryImpl();

    /**
     * Consultar histórico usando filtro dinâmico.
     */
    public List<Atendimento> consultarHistorico(
            Long ambulanciaId,
            GravidadeOcorrencia gravidade,
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        return atendimentoRepository.consultarHistorico(
                ambulanciaId,
                inicio,
                fim,
                gravidade
        );
    }

    /**
     * Consulta tempo médio de resposta por gravidade.
     * Retorna um mapa tipo:
     * ALTA -> 4.2 minutos
     * MEDIA -> 7.9 minutos
     */
    public Map<GravidadeOcorrencia, Double> tempoMedioResposta() {

        Map<GravidadeOcorrencia, Double> mapa = new LinkedHashMap<>();

        for (GravidadeOcorrencia g : GravidadeOcorrencia.values()) {
            Double media = atendimentoRepository.tempoMedioRespostaPorGravidade(g);

            if (media != null) {
                // EXTRACT(EPOCH) retorna segundos → convertendo para minutos:
                mapa.put(g, media / 60.0);
            }
        }

        return mapa;
    }

    /**
     * Quantidade de ocorrências por bairro.
     * Retorna:
     * "Centro" -> 32
     * "Vila Nova" -> 15
     */
    public Map<String, Long> ocorrenciasPorBairro() {

        List<Object[]> dados = atendimentoRepository.mapaOcorrenciasPorBairro();
        Map<String, Long> mapa = new LinkedHashMap<>();

        for (Object[] linha : dados) {
            String bairro = (String) linha[0];
            Long qtd = (Long) linha[1];

            mapa.put(bairro, qtd);
        }

        return mapa;
    }
}
