package mhd.sosrota.infrastructure;

import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.repository.*;
import mhd.sosrota.service.AmbulanciaService;
import mhd.sosrota.service.GrafoCidadeService;
import mhd.sosrota.service.ProfissionalService;
import mhd.sosrota.service.UsuarioService;

public class AppContext {
    private static AppContext instance;
    private final UsuarioService usuarioService;
    private final GrafoCidadeService grafoService;
    private final AmbulanciaService ambulanciaService;
    private final ProfissionalService profissionalService;

    private Ambulancia ambulanciaEmEdicao;
    private Profissional profissionalEmEdicao;

    private AppContext() {
        UsuarioRepository usuarioRepository = new UsuarioRepositoryImpl();
        this.usuarioService = new UsuarioService(usuarioRepository);

        BairroRepository bairroRepository = new BairroRepositoryImpl();
        RuaRepository ruaRepository = new RuaRepositoryImpl();
        this.grafoService = new GrafoCidadeService(bairroRepository, ruaRepository);

        AmbulanciaRepository ambulanciaRepository = new AmbulanciaRepositoryImpl();
        this.ambulanciaService = new AmbulanciaService(ambulanciaRepository);

        ProfissionalRepository profissionalRepository = new ProfissionalRepositoryImpl();
        this.profissionalService = new ProfissionalService(profissionalRepository);
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

    public Ambulancia getAmbulanciaEmEdicao() {
        return ambulanciaEmEdicao;
    }

    public Profissional getProfissionalEmEdicao() {
        return profissionalEmEdicao;
    }

    public ProfissionalService getProfissionalService() {
        return profissionalService;
    }

    public void setAmbulanciaEmEdicao(Ambulancia ambulanciaEmEdicao) {
        this.ambulanciaEmEdicao = ambulanciaEmEdicao;
    }

    public void setProfissionalEmEdicao(Profissional profissionalEmEdicao) {
        this.profissionalEmEdicao = profissionalEmEdicao;
    }
}
