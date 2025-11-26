package mhd.sosrota.presentation;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 25/11/2025
 * @brief Class PasswordToggle
 */
public class PasswordToggle {
    private final PasswordField hidden;
    private final TextField visible;
    private final ImageView icon;
    private boolean showing = false;

    private final Image showingImage = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/images/visibility_on.png"))
    );
    private final Image hidingImage = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/images/visibility_off.png"))
    );

    public PasswordToggle(PasswordField hidden, TextField visible, ImageView icon) {
        this.hidden = hidden;
        this.visible = visible;
        this.icon = icon;

        // sincroniza digitando em qualquer campo
        hidden.textProperty().addListener((_, _, nv) -> {
            if (!showing) visible.setText(nv);
        });
        visible.textProperty().addListener((_, _, nv) -> {
            if (showing) hidden.setText(nv);
        });

        setShowing(false);
    }

    public void toggle() {
        setShowing(!showing);
    }

    public void setShowing(boolean value) {
        showing = value;

        hidden.setVisible(!value);
        hidden.setManaged(!value);

        visible.setVisible(value);
        visible.setManaged(value);

        icon.setImage(value ? showingImage : hidingImage);
    }
}

