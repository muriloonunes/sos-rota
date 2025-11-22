module mhd.sosrota {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.girod.javafx.svgimage;

    opens mhd.sosrota to javafx.fxml;
    exports mhd.sosrota;
    opens mhd.sosrota.controller to javafx.fxml;
    exports mhd.sosrota.controller;
}