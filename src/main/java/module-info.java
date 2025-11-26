module mhd.sosrota {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.girod.javafx.svgimage;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires org.postgresql.jdbc;
    requires java.prefs;

    opens mhd.sosrota to javafx.fxml;
    exports mhd.sosrota;
    opens mhd.sosrota.controller to javafx.fxml;
    exports mhd.sosrota.controller;
}