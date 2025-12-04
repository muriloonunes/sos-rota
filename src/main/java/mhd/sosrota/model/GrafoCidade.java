package mhd.sosrota.model;

import java.util.*;

public class GrafoCidade {
    private List<Bairro> bairros;
    private List<Rua> ruas;

    private transient Map<Bairro, List<Rua>> adjacencia;

    public GrafoCidade() {
    }

    public GrafoCidade(List<Bairro> bairros, List<Rua> ruas) {
        this.bairros = bairros;
        this.ruas = ruas;
        construirAdjacencia();
    }

    public void construirAdjacencia() {
        this.adjacencia = new HashMap<>();
        for (Bairro b : bairros) {
            adjacencia.put(b, new ArrayList<>());
        }
        for (Rua rua : ruas) {
            // grafo nao direcionado
            adjacencia.computeIfAbsent(rua.getOrigem(), _ -> new ArrayList<>()).add(rua);
            adjacencia.computeIfAbsent(rua.getDestino(), _ -> new ArrayList<>()).add(rua);
        }
    }

    public Map<Bairro, Double> calcularDistanciasParaTodos(Bairro origem) {
        Map<Bairro, Double> distancias = new HashMap<>();
        Set<Bairro> visitados = new HashSet<>();
        // PriorityQueue para pegar sempre o mais próximo (Performance O(E log V))
        PriorityQueue<NoDijkstra> filaPrioridade = new PriorityQueue<>();

        // Inicialização
        for (Bairro b : bairros) {
            distancias.put(b, Double.POSITIVE_INFINITY);
        }
        distancias.put(origem, 0.0);
        filaPrioridade.add(new NoDijkstra(origem, 0.0));

        while (!filaPrioridade.isEmpty()) {
            NoDijkstra atualNo = filaPrioridade.poll();
            Bairro atual = atualNo.bairro;

            if (visitados.contains(atual)) continue;
            visitados.add(atual);

            List<Rua> ruasSaida = adjacencia.getOrDefault(atual, Collections.emptyList());

            for (Rua rua : ruasSaida) {
                Bairro vizinho = rua.getOrigem().equals(atual) ? rua.getDestino() : rua.getOrigem();

                if (!visitados.contains(vizinho)) {
                    double novaDist = distancias.get(atual) + rua.getDistanciaKm();

                    if (novaDist < distancias.get(vizinho)) {
                        distancias.put(vizinho, novaDist);
                        filaPrioridade.add(new NoDijkstra(vizinho, novaDist));
                    }
                }
            }
        }
        return distancias;
    }

    public List<Bairro> getBairros() {
        return bairros;
    }

    public void setBairros(List<Bairro> bairros) {
        this.bairros = bairros;
    }

    public List<Rua> getRuas() {
        return ruas;
    }

    public void setRuas(List<Rua> ruas) {
        this.ruas = ruas;
    }

    private static class NoDijkstra implements Comparable<NoDijkstra> {
        Bairro bairro;
        double distancia;

        public NoDijkstra(Bairro bairro, double distancia) {
            this.bairro = bairro;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NoDijkstra outro) {
            return Double.compare(this.distancia, outro.distancia);
        }
    }
}
