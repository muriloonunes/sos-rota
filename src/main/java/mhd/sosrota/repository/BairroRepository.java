package mhd.sosrota.repository;

import mhd.sosrota.model.Bairro;

import java.util.List;

public interface BairroRepository {
    List<Bairro> obterBairros();
    Bairro encontrarPorId(long id);
}
