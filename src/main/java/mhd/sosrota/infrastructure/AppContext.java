package mhd.sosrota.infrastructure;

import mhd.sosrota.repository.*;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.service.GrafoCidadeService;
import mhd.sosrota.service.UsuarioService;

public class AppContext {
    private static AppContext instance;
    private final UsuarioService usuarioService;
    private final GrafoCidadeService grafoService;
    private final AmbulanciaService ambulanciaService;

    private AppContext() {
        UsuarioRepository usuarioRepository = new UsuarioRepositoryImpl();
        this.usuarioService = new UsuarioService(usuarioRepository);

        BairroRepository bairroRepository = new BairroRepositoryImpl();
        RuaRepository ruaRepository = new RuaRepositoryImpl();
        this.grafoService = new GrafoCidadeService(bairroRepository, ruaRepository);

        AmbulanciaRepository ambulanciaRepository = new AmbulanciaRepositoryImpl();
        this.ambulanciaService = new AmbulanciaService(ambulanciaRepository);
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    public GrafoCidadeService getGrafoService() {
        return grafoService;
    }

    public AmbulanciaService getAmbulanciaService() {
        return ambulanciaService;
    }
}
