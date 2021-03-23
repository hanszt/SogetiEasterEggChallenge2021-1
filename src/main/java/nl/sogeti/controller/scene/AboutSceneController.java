package nl.sogeti.controller.scene;

import nl.sogeti.controller.SceneManager;
import nl.sogeti.service.AboutService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import nl.sogeti.service.IAboutService;

import java.io.IOException;

import static nl.sogeti.model.AppConstants.INIT_SCENE_DIMENSION;
import static nl.sogeti.model.AppConstants.FxmlFile.ABOUT_SCENE;
import static nl.sogeti.model.AppConstants.FxmlFile.MAIN_SCENE;

public class AboutSceneController extends SceneController {

    @FXML
    private ComboBox<IAboutService.AboutText> textComboBox;
    @FXML
    private TextArea textArea;

    private final AboutService aboutService = new AboutService();

    public AboutSceneController(SceneManager sceneManager) throws IOException {
        super(ABOUT_SCENE.getFxmlFileName(), sceneManager);
    }

    @Override
    public void setup() {
        textArea.setPrefSize(INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
        textArea.setEditable(false);
        aboutService.loadContent().forEach(aboutText -> textComboBox.getItems().add(aboutText));
        textComboBox.setValue(textComboBox.getItems().get(0));
    }


    @FXML
    public void goBack() {
        sceneManager.setupScene(MAIN_SCENE);
    }

    @FXML
    private void textComboboxAction() {
        textArea.setText(textComboBox.getValue().getText());
    }

    protected SceneController getPojo() {
        return this;
    }

}
