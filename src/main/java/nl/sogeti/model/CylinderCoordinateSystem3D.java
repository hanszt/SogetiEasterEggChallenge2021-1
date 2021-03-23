package nl.sogeti.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;

public class CylinderCoordinateSystem3D extends CoordinateSystem3D {

    public CylinderCoordinateSystem3D() {
        PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
        PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        Shape3D xAxis = new Cylinder(1, 500);
        xAxis.setRotationAxis(Rotate.Z_AXIS);
        xAxis.setRotate(90);
        xAxis.setMaterial(redMaterial);
        Shape3D yAxis = new Cylinder(1, 500);
        yAxis.setMaterial(greenMaterial);
        Shape3D zAxis = new Cylinder(1, 500);
        zAxis.setRotationAxis(Rotate.X_AXIS);
        zAxis.setRotate(90);
        zAxis.setMaterial(blueMaterial);
        getChildren().addAll(xAxis, yAxis, zAxis);
    }
}
