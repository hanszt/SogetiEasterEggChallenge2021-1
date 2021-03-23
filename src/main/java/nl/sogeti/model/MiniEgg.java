package nl.sogeti.model;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

public class MiniEgg extends Group {

    final Shape3D body;
    final PhongMaterial material = new PhongMaterial();

    public MiniEgg(Shape3D body) {
        this.body = body;
        configureComponents();
        getChildren().add(body);
    }

    public ObjectProperty<Color> diffuseColorProperty() {
        return material.diffuseColorProperty();
    }

    private void configureComponents() {
        body.setMaterial(material);
        material.setSpecularColor(Color.WHITE);
    }

}
