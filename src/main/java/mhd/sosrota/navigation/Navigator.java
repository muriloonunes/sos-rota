package mhd.sosrota.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 22/11/2025
 * @brief Class Navigator
 */
public class Navigator {
    private final int APP_WIDTH = 1430;
    private final int APP_HEIGHT = 810;

    private final Stage stage;

    public Navigator(Stage stage) {
        this.stage = stage;
    }

    public void navigate(Screens screen) {
        loadScreen(screen);
    }

    private void loadScreen(Screens screen) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(screen.getFxmlPath()));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof Navigable navigableController) {
                navigableController.setNavigator(this);
            }

            Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Carrega um FXML para a área de conteúdo principal do Dashboard.
     *
     * @param screen A tela que será carregada.
     */
    public void setContent(Pane container, Screens screen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.getFxmlPath()));
            Parent view = loader.load();

            // Injeção do Navigator no controller se implementar Navigable
            Object controller = loader.getController();
            if (controller instanceof Navigable navController) {
                navController.setNavigator(this);
            }

            // Substitui o conteúdo atual do container
            container.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela: " + screen);
        }
    }

    public void showModal(Screens screen, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(screen.getFxmlPath()));
            Parent root = loader.load();

            // Injeta o Navigator no controller, se implementar Navigable
            Object controller = loader.getController();
            if (controller instanceof Navigable navController) {
                navController.setNavigator(this);
            }

            // Cria um novo Stage modal
            Stage modalStage = new Stage();
            modalStage.initOwner(stage); // stage é o Stage principal
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait(); // bloqueia o Stage principal até fechar

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao abrir modal: " + screen);
        }
    }
}
