package nl.sogeti.controller.scene;

import nl.sogeti.controller.AppManager;
import nl.sogeti.controller.FXMLController;
import nl.sogeti.controller.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalTime;

import static nl.sogeti.model.AppConstants.INIT_SCENE_DIMENSION;

public abstract class SceneController extends FXMLController {

    private boolean setup;

    protected final SceneManager sceneManager;
    protected final Scene scene;
    protected final LocalTime startTimeScene;

    protected SceneController(String fxmlFileName, SceneManager sceneManager) throws IOException {
        super(fxmlFileName);
        this.startTimeScene = LocalTime.now();
        this.sceneManager = sceneManager;
        scene = new Scene(getRoot(), INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
    }

    public abstract void setup();

    @FXML
    public void newInstance() {
       new AppManager(new Stage()).start();
    }

    @FXML
    void quitInstance() {
        sceneManager.getStage().close();
        sceneManager.printClosingText();
    }

    @FXML
    void exitProgram() {
        System.exit(0);
    }

    public boolean isSetup() {
        boolean temp = setup;
        setup = true;
        return temp;
    }

    public Scene getScene() {
        return scene;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public LocalTime getStartTimeScene() {
        return startTimeScene;
    }
}
