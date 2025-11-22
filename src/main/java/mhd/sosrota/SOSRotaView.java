package mhd.sosrota;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.navigation.Screens;

import java.util.Objects;

public class SOSRotaView extends Application {
    @Override
    public void start(Stage stage) {
        Navigator navigator = new Navigator(stage);
        navigator.navigate(Screens.TELA_LOGIN);
//        FXMLLoader fxmlLoader = FXMLLoaderHelper.loadFXML(TELA_LOGIN);
//        Parent root = fxmlLoader.getRoot();
//        Scene scene = FXMLLoaderHelper.createScene(root);
        stage.getIcons().add(new Image(Objects.requireNonNull(SOSRotaView.class.getResourceAsStream("/images/icon.png"))));
        stage.setTitle("SOS Rota");
//        stage.setScene(scene);
        stage.show();
    }
}
