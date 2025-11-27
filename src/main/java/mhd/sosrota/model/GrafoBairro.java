package mhd.sosrota.model;

import java.util.*;

public class GrafoBairro {
    private List<Bairro> bairros;
    private List<Rua> ruas;

    public GrafoBairro(List<Bairro> bairros, List<Rua> ruas) {
        this.bairros = bairros;
        this.ruas = ruas;
    }

    public List<Bairro> vizinhos(Bairro bairro) {
        List<Bairro> vizinhos = new ArrayList<>();

        for (Rua rua : ruas) {
            if (rua.getOrigem().equals(bairro)) {
                vizinhos.add(rua.getDestino());
            } else if (rua.getDestino().equals(bairro)) {
                vizinhos.add(rua.getOrigem());
            }
        }
        return vizinhos;
    }

    public List<Bairro> menorCaminho(GrafoBairro grafo, Bairro origem, Bairro destino) {

        Map<Bairro, Double> dist = new HashMap<>();
        Map<Bairro, Bairro> anterior = new HashMap<>();
        Set<Bairro> visitados = new HashSet<>();

        for (Bairro b : grafo.getBairros()) {
            dist.put(b, Double.POSITIVE_INFINITY);
            anterior.put(b, null);
        }

        dist.put(origem, 0.0);

        while (visitados.size() < grafo.getBairros().size()) {

            Bairro atual = null;
            double menor = Double.POSITIVE_INFINITY;

            for (var e : dist.entrySet()) {
                if (!visitados.contains(e.getKey()) && e.getValue() < menor) {
                    atual = e.getKey();
                    menor = e.getValue();
                }
            }

            if (atual == null) break;
            if (atual.equals(destino)) break;

            visitados.add(atual);

            for (Bairro viz : grafo.vizinhos(atual)) {
                double novaDist = dist.get(atual) + grafo.distancia(atual, viz);

                if (novaDist < dist.get(viz)) {
                    dist.put(viz, novaDist);
                    anterior.put(viz, atual);
                }
            }
        }

        // reconstrói o caminho
        LinkedList<Bairro> caminho = new LinkedList<>();
        Bairro atual = destino;

        while (atual != null) {
            caminho.addFirst(atual);
            atual = anterior.get(atual);
        }

        return caminho;
    }

    public double distancia(Bairro a, Bairro b) {
        for (Rua rua : ruas) {
            if (rua.getOrigem().equals(a) && rua.getDestino().equals(b) ||
                    rua.getOrigem().equals(b) && rua.getDestino().equals(a)) {
                return rua.getDistanciaKm();
            }
        }
        return Double.POSITIVE_INFINITY;
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
}
