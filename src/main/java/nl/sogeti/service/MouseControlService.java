package nl.sogeti.service;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;

public class MouseControlService implements IMouseControlService{

    private double mouseAnchorX;
    private double mouseAnchorY;

    private double nodeAnchorTranslateX = 0;
    private double nodeAnchorTranslateY = 0;

    private double nodeAnchorAngleX = 0;
    private double nodeAnchorAngleY = 0;

    private final DoubleProperty angleX = new SimpleDoubleProperty();
    private final DoubleProperty angleY = new SimpleDoubleProperty();

    private final Node target;

    public MouseControlService(Node target) {
        this.target = target;
    }

    public void initMouseControl(Node reference) {
        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
        target.getTransforms().addAll(xRotate, yRotate);

        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        reference.setOnMousePressed(event -> mousePressedEvent(target, event));
        reference.setOnMouseDragged(event -> mouseDraggedEvent(target, event));
        reference.addEventHandler(ScrollEvent.SCROLL, event -> mouseScrollEvent(target, event));
    }

    private void mousePressedEvent(Node node, MouseEvent event) {
        mouseAnchorX = event.getX();
        mouseAnchorY = event.getY();
        if (event.isPrimaryButtonDown()) {
            nodeAnchorTranslateX = node.getTranslateX();
            nodeAnchorTranslateY = node.getTranslateY();
        } else if (event.isSecondaryButtonDown() || event.isMiddleButtonDown()) {
            nodeAnchorAngleX = angleX.get();
            nodeAnchorAngleY = angleY.get();
        }
    }

    private void mouseDraggedEvent(Node node, MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            double deltaX = mouseAnchorX - event.getX();
            double deltaY = mouseAnchorY - event.getY();
            node.setTranslateX(nodeAnchorTranslateX - deltaX);
            node.setTranslateY(nodeAnchorTranslateY - deltaY);
        } else if (event.isSecondaryButtonDown() || event.isMiddleButtonDown()) {
            angleX.set(nodeAnchorAngleX - (mouseAnchorY - event.getY()));
            angleY.set(nodeAnchorAngleY + (mouseAnchorX - event.getX()));
        }
    }

    private void mouseScrollEvent(Node node, ScrollEvent event) {
        double delta = event.getDeltaY();
        node.setTranslateZ(node.getTranslateZ() - delta);
    }

    public void setOrientation(double angleX, double angleY) {
        this.angleX.set(angleX);
        this.angleY.set(angleY);
    }

    public void setTargetTranslation(Point3D point3D) {
        target.setTranslateX(point3D.getX());
        target.setTranslateY(point3D.getY());
        target.setTranslateZ(point3D.getZ());
    }

    public double getTargetTranslateX() {
        return target.getTranslateX();
    }

    public double getTargetTranslateY() {
        return target.getTranslateY();
    }

    public double getTargetTranslateZ() {
        return target.getTranslateZ();
    }

    public double getAngleX() {
        return angleX.get();
    }

    public double getAngleY() {
        return angleY.get();
    }

}
