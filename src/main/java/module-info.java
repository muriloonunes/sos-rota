module mhd.sosrota {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.girod.javafx.svgimage;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.prefs;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires com.google.gson;
    requires javafx.base;

    opens mhd.sosrota to javafx.fxml;
    exports mhd.sosrota;
    opens mhd.sosrota.controller to javafx.fxml;
    exports mhd.sosrota.controller;
    opens mhd.sosrota.model to org.hibernate.orm.core, com.google.gson, javafx.base;
    exports mhd.sosrota.navigation;
}