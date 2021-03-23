package nl.sogeti.controller;

import nl.sogeti.model.AppConstants;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;

public class AppManager {

    private static final Logger LOGGER = LogManager.getLogger(AppManager.class);

    private static int instances = 0;
    private final int instance;
    final Stage stage;
    private final SceneManager sceneManager;

    public AppManager(Stage stage) {
        this.stage = stage;
        this.sceneManager = new SceneManager(this);
        this.instance = ++instances;
    }

    public void start() {
        String startingMessage = String.format("Starting instance %d of %s at %s...%n",
                instance, AppConstants.TITLE, sceneManager.getCurSceneController()
                        .getStartTimeScene()
                        .format(DateTimeFormatter.ofPattern("hh:mm:ss")));
        LOGGER.info(startingMessage);
        sceneManager.setupScene(AppConstants.FxmlFile.MAIN_SCENE);
        configureStage(stage);

        stage.show();
        String startedMessage = String.format("instance %d started%n", instance);
        LOGGER.info(startedMessage);
    }

    public void configureStage(Stage stage) {
        stage.setTitle(String.format("%s (%d)", AppConstants.TITLE, instance));
        stage.setMinWidth(AppConstants.MIN_STAGE_DIMENSION.getWidth());
        stage.setMinHeight(AppConstants.MIN_STAGE_DIMENSION.getHeight());
        stage.setOnCloseRequest(e -> sceneManager.printClosingText());
        String fileName = AppConstants.getProperty("logo_uri", "");
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        if (inputStream != null) stage.getIcons().add(new Image(inputStream));
        else LOGGER.warn("Input stream to " + fileName + " was null");
    }

    public int getInstance() {
        return instance;
    }
}
