package nl.sogeti.model;

import nl.sogeti.model.controls3d.Translation3DKeyFilter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class MovableCameraPlatform extends Group {

    private final PerspectiveCamera camera = new PerspectiveCamera(true);//fixedEyeAtCameraZero
    private final Translation3DKeyFilter translation3DKeyFilter = new Translation3DKeyFilter();

    private final DoubleProperty maxVelocity = new SimpleDoubleProperty();

    private double velocity;

    public MovableCameraPlatform() {
        super();
        translation3DKeyFilter.userInputVectorSizeProperty().bindBidirectional(maxVelocityProperty());
        setInitCameraConfig();
        this.getChildren().add(camera);
    }

    private void setInitCameraConfig() {
        camera.setFarClip(10_000);
        camera.setNearClip(.01);
    }

    public void update(Duration deltaDuration) {
        updateTranslation(deltaDuration);
    }

    private void updateTranslation(Duration deltaDuration) {
        double deltaTSeconds = deltaDuration.toSeconds();
        Point3D velocityVector = translation3DKeyFilter.getUserInputVector();
        velocity = velocityVector.magnitude();
        Point3D position = new Point3D(getTranslateX(), getTranslateY(), getTranslateZ());
        position = position.add(velocityVector.multiply(deltaTSeconds));
        setTranslateX(position.getX());
        setTranslateY(position.getY());
        setTranslateZ(position.getZ());
    }

    public void addKeyControls(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, translation3DKeyFilter.getKeyPressed());
        scene.addEventFilter(KeyEvent.KEY_RELEASED, translation3DKeyFilter.getKeyReleased());
    }

    public void setTranslate(Translate translate) {
        setTranslateX(translate.getX());
        setTranslateY(translate.getY());
        setTranslateZ(translate.getZ());
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public double getVelocity() {
        return velocity;
    }

    public DoubleProperty maxVelocityProperty() {
        return maxVelocity;
    }

}
