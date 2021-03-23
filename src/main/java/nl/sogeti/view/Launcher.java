package nl.sogeti.view;

import nl.sogeti.controller.AppManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        new AppManager(stage).start();
    }

}
