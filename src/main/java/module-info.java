module it.unina.ingSw.cineMates {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires spring.security.crypto;
    requires spring.web;
    requires spring.core;

    opens it.unina.ingSw.cineMates20.controller to javafx.fxml;
    exports it.unina.ingSw.cineMates20;
}