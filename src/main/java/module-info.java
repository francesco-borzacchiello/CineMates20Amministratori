module it.unina.ingSw.cineMates {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;

    opens it.unina.ingSw.cineMates20.controller to javafx.fxml;
    exports it.unina.ingSw.cineMates20;
}