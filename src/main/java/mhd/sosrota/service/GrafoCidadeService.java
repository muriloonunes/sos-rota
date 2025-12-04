package mhd.sosrota.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.GrafoCidade;
import mhd.sosrota.model.Rua;
import mhd.sosrota.repository.BairroRepository;
import mhd.sosrota.repository.RuaRepository;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GrafoCidadeService {
    private final BairroRepository bairroRepository;
    private final RuaRepository ruaRepository;
    private static final String CACHE_FILE = "grafo.json";
    private final Gson gson;

    private GrafoCidade grafo;

    public GrafoCidadeService(BairroRepository bairroRepository, RuaRepository ruaRepository) {
        this.bairroRepository = bairroRepository;
        this.ruaRepository = ruaRepository;
        this.gson = new GsonBuilder().setPrettyPrinting().setStrictness(Strictness.LENIENT).create();
    }

    public GrafoCidade obterGrafo() {
        if (this.grafo == null) {
            this.grafo = carregarDoDisco();

            if (this.grafo == null) {
                List<Bairro> bairros = bairroRepository.obterBairros();
                List<Rua> ruas = ruaRepository.obterRuas();
                this.grafo = new GrafoCidade(bairros, ruas);

                salvarNoDisco(this.grafo);
            }
        }
        return this.grafo;
    }

    private GrafoCidade carregarDoDisco() {
        File file = new File(CACHE_FILE);

        if (!file.exists()) {
            return null;
        }

        try (Reader reader = new FileReader(file)) {
            GrafoCidade grafo = gson.fromJson(reader, GrafoCidade.class);

            Map<Long, Bairro> mapaBairros = grafo.getBairros().stream()
                    .collect(Collectors.toMap(Bairro::getId, Function.identity()));

            for (Rua rua : grafo.getRuas()) {
                rua.setOrigem(mapaBairros.get(rua.getOrigem().getId()));
                rua.setDestino(mapaBairros.get(rua.getDestino().getId()));
            }

            grafo.construirAdjacencia();

            return grafo;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            file.delete();
            return null;
        }
    }

    private void salvarNoDisco(GrafoCidade grafo) {
        try (Writer writer = new FileWriter(CACHE_FILE)) {
            gson.toJson(grafo, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Bairro> obterBairros() {
        return obterGrafo().getBairros();
    }

    public List<Rua> obterRuas() {
        return obterGrafo().getRuas();
    }
}
