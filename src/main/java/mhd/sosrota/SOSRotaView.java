package mhd.sosrota;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mhd.sosrota.infrastructure.UserPrefs;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;

public class SOSRotaView extends Application {
    @Override
    public void start(Stage stage) {
        System.setProperty("prism.lcdtext", "false");
        loadFonts();
        Navigator navigator = new Navigator(stage);

        UserPrefs prefs = new UserPrefs();
        if (prefs.existeUsuarioSalvo()) {
            navigator.navigate(Screens.TELA_APP);
        } else {
            navigator.navigate(Screens.TELA_LOGIN);
        }
        stage.show();
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 14);
    }
}
