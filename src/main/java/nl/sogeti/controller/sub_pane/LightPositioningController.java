package nl.sogeti.controller.sub_pane;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import nl.sogeti.controller.FXMLController;
import nl.sogeti.controller.scene.MainSceneController;
import nl.sogeti.model.AppConstants;
import nl.sogeti.model.VisiblePointLight;

import java.io.IOException;

import static javafx.animation.Animation.Status.*;

public class LightPositioningController extends FXMLController {

    @FXML
    private Slider lightXAxisRotationSlider;
    @FXML
    private Slider lightYAxisRotationSlider;
    @FXML
    private Slider lightPivotZSlider;
    @FXML
    public Slider translateXSlider;
    @FXML
    public Slider translateYSlider;
    @FXML
    public Slider translateZSlider;

    private final MainSceneController mainSceneController;

    public LightPositioningController(MainSceneController mainSceneController) throws IOException {
        super(AppConstants.FxmlFile.LIGHT_POSITIONING_PANE.getFxmlFileName());
        this.mainSceneController = mainSceneController;
        recenter();
    }

    public void addVisibleLightPositioning(VisiblePointLight visiblePointLight) {
        final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        Translate pivot = new Translate();
        Translate translate = new Translate();
        visiblePointLight.bindPivotSphereToPivot(pivot);
        visiblePointLight.getTransforms().addAll(translate, rotateY, rotateX, pivot);

        rotateX.angleProperty().bindBidirectional(lightXAxisRotationSlider.valueProperty());
        rotateY.angleProperty().bindBidirectional(lightYAxisRotationSlider.valueProperty());
        pivot.zProperty().bindBidirectional(lightPivotZSlider.valueProperty());

        translate.xProperty().bindBidirectional(translateXSlider.valueProperty());
        translate.yProperty().bindBidirectional(translateYSlider.valueProperty());
        translate.zProperty().bindBidirectional(translateZSlider.valueProperty());
        configureAnimatedLightRotation(lightXAxisRotationSlider, X_ROTATE_EGG_LIGHT_ON_OFF, false);
        configureAnimatedLightRotation(lightYAxisRotationSlider, Y_ROTATE_EGG_LIGHT_ON_OFF, true);
    }

    private void configureAnimatedLightRotation(Slider lightRotationSlider, String refKey, boolean play) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(lightRotationSlider.valueProperty(), -180)),
                new KeyFrame(Duration.seconds(4), new KeyValue(lightRotationSlider.valueProperty(), 180)));
        timeline.setCycleCount(Animation.INDEFINITE);
        if (play) timeline.playFromStart();

        Scene scene = mainSceneController.getScene();
        scene.addEventFilter(KeyEvent.KEY_TYPED, e -> pausePlayAnimation(e, timeline, refKey));
        lightRotationSlider.setOnMousePressed(e -> timeline.pause());
    }

    private static final String X_ROTATE_EGG_LIGHT_ON_OFF = AppConstants.getProperty(
            "rotate_x_egg_light_on_off_key", "o");
    private static final String Y_ROTATE_EGG_LIGHT_ON_OFF = AppConstants.getProperty(
            "rotate_y_egg_light_on_off_key", "p");

    private void pausePlayAnimation(KeyEvent e, Animation animation, String refKey) {
        if (e.getCharacter().equals(refKey)) {
            if (animation.getStatus().equals(RUNNING)) animation.pause();
            else animation.play();
        }
    }

    public void recenter() {
        translateXSlider.setValue(AppConstants.INIT_EGG_LIGHT_TRANSLATION.getX());
        translateYSlider.setValue(AppConstants.INIT_EGG_LIGHT_TRANSLATION.getY());
        translateZSlider.setValue(AppConstants.INIT_EGG_LIGHT_TRANSLATION.getZ());

        lightXAxisRotationSlider.setValue(AppConstants.INIT_EGG_LIGHT_ANGLE_X);
        lightYAxisRotationSlider.setValue(AppConstants.INIT_EGG_LIGHT_ANGLE_Y);
        lightPivotZSlider.setValue(AppConstants.INIT_EGG_LIGHT_Z_PIVOT);
    }

    @Override
    protected FXMLController getPojo() {
        return this;
    }
}
