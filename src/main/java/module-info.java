module Main.app {

    requires javafx.fxml;
    requires javafx.controls;
    requires org.jetbrains.annotations;
    requires log4j;

    opens nl.sogeti.controller.scene to javafx.fxml;
    opens nl.sogeti.controller.sub_pane to javafx.fxml;
    opens nl.sogeti.view to javafx.graphics;
}
