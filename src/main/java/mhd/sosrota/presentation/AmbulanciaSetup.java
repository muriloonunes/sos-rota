package mhd.sosrota.presentation;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Bairro;
import mhd.sosrota.model.enums.StatusAmbulancia;
import mhd.sosrota.model.enums.TipoAmbulancia;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 29/11/2025
 * @brief Class AmbulanciaSetup
 */
public class AmbulanciaSetup {
    public static void configurarCampos(TextField placaField, ComboBox<String> tipoComboBox, ComboBox<String> statusComboBox, ComboBox<Bairro> baseComboBox) {
        List<Bairro> bases = AppContext.getInstance().getGrafoService().obterBairros().stream().filter(
                Bairro::temBase
        ).toList();

        tipoComboBox.getItems().addAll(
                Arrays.stream(TipoAmbulancia.values())
                        .map(TipoAmbulancia::getDescricao)
                        .toList()
        );

        statusComboBox.getItems().addAll(
                Arrays.stream(StatusAmbulancia.values())
                        .map(StatusAmbulancia::getDescricao)
                        .toList()
        );

        baseComboBox.getItems().addAll(
                bases
        );

        placaField.textProperty().addListener((_, _, newValue) -> {
            if (placaField.getText().length() > 7) {
                placaField.setText(newValue.substring(0, 7));
            }
        });
    }
}
