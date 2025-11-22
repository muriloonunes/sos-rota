package mhd.sosrota;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mhd.sosrota.util.FXMLLoaderHelper;

import java.io.IOException;
import java.util.Objects;

import static mhd.sosrota.SOSRota.*;

public class SOSRotaView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = FXMLLoaderHelper.loadFXML(TELA_LOGIN);
        Parent root = fxmlLoader.getRoot();
        Scene scene = FXMLLoaderHelper.createScene(root);
        stage.getIcons().add(new Image(Objects.requireNonNull(SOSRotaView.class.getResourceAsStream("/images/icon.png"))));
        stage.setTitle("SOS Rota");
        stage.setScene(scene);
        stage.show();
    }
}
