package mhd.sosrota;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mhd.sosrota.util.FXMLLoaderHelper;

import java.io.IOException;

import static mhd.sosrota.SOSRota.TELA_LOGIN;

public class SOSRotaView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = FXMLLoaderHelper.loadFXML(TELA_LOGIN);
        Scene scene = new Scene(fxmlLoader.getRoot());
        stage.setTitle("SOS Rota");
        stage.setScene(scene);
        stage.show();
    }
}
