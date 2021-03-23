package nl.sogeti.model.controls3d;

import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;

import static javafx.scene.input.KeyCode.*;

public class Translation3DKeyFilter extends Abstract3DKeyFilter {

    private Point3D userInputVector = Point3D.ZERO;

    public Translation3DKeyFilter() {
        this(A, D, W, S, F, R);
    }

    public Translation3DKeyFilter(KeyCode left, KeyCode right,
                                  KeyCode up, KeyCode down,
                                  KeyCode front, KeyCode back) {
        super(left, right, up, down, front, back);
    }

    boolean pressedAction(Point3D vector) {
        userInputVector = userInputVector.add(vector);
        return true;
    }

    boolean releasedAction(Point3D vector) {
        userInputVector = userInputVector.subtract(vector);
        return false;
    }

    void allReleasedAction(boolean allReleased) {
        if (allReleased) userInputVector = Point3D.ZERO;
    }

    public Point3D getUserInputVector() {
        return userInputVector;
    }

}
