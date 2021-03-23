package nl.sogeti.controller;

import nl.sogeti.controller.scene.AboutSceneController;
import nl.sogeti.controller.scene.MainSceneController;
import nl.sogeti.controller.scene.SceneController;
import javafx.stage.Stage;
import javafx.util.Duration;
import nl.sogeti.model.AppConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

public class SceneManager {

    private static final Logger LOGGER = LogManager.getLogger(SceneManager.class);

    private final AppManager appManager;
    private final Map<AppConstants.FxmlFile, SceneController> sceneControllerMap;
    private SceneController curSceneController;

    public SceneManager(AppManager appManager) {
        this.appManager =appManager;
        sceneControllerMap = loadFrontend();
        curSceneController = sceneControllerMap.get(AppConstants.FxmlFile.MAIN_SCENE);
    }

    private Map<AppConstants.FxmlFile, SceneController> loadFrontend() {
        try {
            return Map.of(AppConstants.FxmlFile.MAIN_SCENE, new MainSceneController(this),
                    AppConstants.FxmlFile.ABOUT_SCENE, new AboutSceneController(this));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.fatal("Something went wrong when loading fxml frontend...");
        }
        return Collections.emptyMap();
    }

    public void setupScene(AppConstants.FxmlFile scene) {
        curSceneController = sceneControllerMap.get(scene);
        appManager.stage.setScene(curSceneController.getScene());
        if (!curSceneController.isSetup()) {
            String message = "setting up " + scene.formattedName() + "...";
            LOGGER.info(message);
            curSceneController.setup();
        }
    }

    public void printClosingText() {
        LocalTime startTimeSim = curSceneController.getStartTimeScene();
        LocalTime stopTimeSim = LocalTime.now();
        Duration runTimeSim = Duration.millis((stopTimeSim.toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        String message = String.format("%s%nAnimation Runtime of instance %d: %.2f seconds%n%s%n", AppConstants.CLOSING_MESSAGE,
                appManager.getInstance(), runTimeSim.toSeconds(), AppConstants.DOTTED_LINE);
        LOGGER.info(message);
    }


    public Stage getStage() {
        return appManager.stage;
    }

    public SceneController getCurSceneController() {
        return curSceneController;
    }

    public Map<AppConstants.FxmlFile, SceneController> getSceneControllerMap() {
        return sceneControllerMap;
    }
}
