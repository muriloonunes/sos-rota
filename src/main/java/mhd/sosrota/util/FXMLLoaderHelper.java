package mhd.sosrota.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

import static mhd.sosrota.SOSRota.APP_HEIGHT;
import static mhd.sosrota.SOSRota.APP_WIDTH;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 17/11/2025
 * @brief Class FXMLLoaderHelper
 */
public class FXMLLoaderHelper {
    public static FXMLLoader loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(FXMLLoaderHelper.class.getResource(fxmlPath));
        loader.load();
        return loader;
    }

    public static Scene createScene(Parent root) {
        return new Scene(root, APP_WIDTH, APP_HEIGHT);
    }
}
