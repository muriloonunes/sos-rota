package mhd.sosrota.infrastructure;

import mhd.sosrota.infrastructure.database.JpaManager;
import mhd.sosrota.model.Ambulancia;
import mhd.sosrota.model.Equipe;
import mhd.sosrota.model.Ocorrencia;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.repository.*;
import mhd.sosrota.service.*;

public class AppContext {
    private static AppContext instance;
    private final UsuarioService usuarioService;
    private final GrafoCidadeService grafoService;
    private final AmbulanciaService ambulanciaService;
    private final ProfissionalService profissionalService;
    private final EquipeService equipeService;
    private final OcorrenciaService ocorrenciaService;
    private final AtendimentoService atendimentoService;
    private final CicloAtendimentoService cicloAtendimentoService;

    private Ambulancia ambulanciaEmEdicao;
    private Profissional profissionalEmEdicao;
    private Equipe equipeEmEdicao;
    private Ocorrencia ocorrenciaEmEdicao;
    private Ocorrencia ocorrenciaParaDespachar;
    private Ocorrencia ocorrenciaDetalhes;

    private AppContext() {
        UsuarioRepository usuarioRepository = new UsuarioRepositoryImpl();
        this.usuarioService = new UsuarioService(usuarioRepository);

        BairroRepository bairroRepository = new BairroRepositoryImpl();
        RuaRepository ruaRepository = new RuaRepositoryImpl();
        this.grafoService = new GrafoCidadeService(bairroRepository, ruaRepository);

        AtendimentoRepository atendimentoRepository = new AtendimentoRepositoryImpl();
        AmbulanciaRepository ambulanciaRepository = new AmbulanciaRepositoryImpl();
        EquipeRepository equipeRepository = new EquipeRepositoryImpl();
        this.ambulanciaService = new AmbulanciaService(ambulanciaRepository, atendimentoRepository, equipeRepository);

        ProfissionalRepository profissionalRepository = new ProfissionalRepositoryImpl();
        this.profissionalService = new ProfissionalService(profissionalRepository);

        this.equipeService = new EquipeService(equipeRepository, ambulanciaRepository);

        this.ocorrenciaService = new OcorrenciaService(new OcorrenciaRepositoryImpl());

        if (!JpaManager.isOffline()) {
            this.cicloAtendimentoService = new CicloAtendimentoService(JpaManager.getFactory());
            this.atendimentoService = new AtendimentoService(grafoService, ambulanciaRepository, atendimentoRepository, cicloAtendimentoService);
        } else {
            this.cicloAtendimentoService = null;
            this.atendimentoService = null;
        }
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public OcorrenciaService getOcorrenciaService() {
        return ocorrenciaService;
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

    public ProfissionalService getProfissionalService() {
        return profissionalService;
    }

    public AtendimentoService getDespachoService() {
        return atendimentoService;
    }

    public EquipeService getEquipeService() {
        return equipeService;
    }

    public CicloAtendimentoService getCicloAtendimentoService() {
        return cicloAtendimentoService;
    }

    public Ambulancia getAmbulanciaEmEdicao() {
        return ambulanciaEmEdicao;
    }

    public Profissional getProfissionalEmEdicao() {
        return profissionalEmEdicao;
    }

    public void setAmbulanciaEmEdicao(Ambulancia ambulanciaEmEdicao) {
        this.ambulanciaEmEdicao = ambulanciaEmEdicao;
    }

    public void setProfissionalEmEdicao(Profissional profissionalEmEdicao) {
        this.profissionalEmEdicao = profissionalEmEdicao;
    }

    public Equipe getEquipeEmEdicao() {
        return equipeEmEdicao;
    }

    public void setEquipeEmEdicao(Equipe equipeEmEdicao) {
        this.equipeEmEdicao = equipeEmEdicao;
    }

    public Ocorrencia getOcorrenciaEmEdicao() {
        return ocorrenciaEmEdicao;
    }

    public void setOcorrenciaEmEdicao(Ocorrencia ocorrenciaEmEdicao) {
        this.ocorrenciaEmEdicao = ocorrenciaEmEdicao;
    }

    public Ocorrencia getOcorrenciaParaDespachar() {
        return ocorrenciaParaDespachar;
    }

    public void setOcorrenciaParaDespachar(Ocorrencia ocorrenciaParaDespachar) {
        this.ocorrenciaParaDespachar = ocorrenciaParaDespachar;
    }

    public Ocorrencia getOcorrenciaDetalhes() {
        return ocorrenciaDetalhes;
    }

    public void setOcorrenciaDetalhes(Ocorrencia ocorrenciaDetalhes) {
        this.ocorrenciaDetalhes = ocorrenciaDetalhes;
    }
}
