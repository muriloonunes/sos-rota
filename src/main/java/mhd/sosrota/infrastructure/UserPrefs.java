package mhd.sosrota.infrastructure;

import java.util.prefs.Preferences;

public class UserPrefs {
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());

    public void salvarUsuario(String nome, String username) {
        prefs.put("nome", nome);
        prefs.put("username", username);
    }

    public String getNome() {
        return prefs.get("nome", null);
    }

    public String getUsername() {
        return prefs.get("username", null);
    }

    public boolean existeUsuarioSalvo() {
        return getUsername() != null;
    }

    public void limparDados() {
        prefs.remove("nome");
        prefs.remove("username");
    }
}
