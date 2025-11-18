module mhd.sosrota {
    requires javafx.controls;
    requires javafx.fxml;


    opens mhd.sosrota to javafx.fxml;
    exports mhd.sosrota;
    opens mhd.sosrota.controller to javafx.fxml;
    exports mhd.sosrota.controller;
}