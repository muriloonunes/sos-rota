package mhd.sosrota.util;

import javafx.scene.Scene;
import javafx.scene.control.Alert;

public class AlertUtil {
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        styleAlert(alert);

        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        styleAlert(alert);

        alert.showAndWait();
    }

    private static void styleAlert(Alert alert) {
        Scene cenaAlerta = alert.getDialogPane().getScene();
        cenaAlerta.getRoot().setStyle("-fx-background-color: #ECF0F1;");
    }
}
