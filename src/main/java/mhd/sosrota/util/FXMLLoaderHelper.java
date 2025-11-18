package mhd.sosrota.util;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

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
}
