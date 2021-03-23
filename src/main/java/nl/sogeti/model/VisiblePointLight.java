package nl.sogeti.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

import java.nio.ByteBuffer;

public class VisiblePointLight extends MiniEgg {

    private final PointLight pointLight;
    private final Sphere visiblePivot = new Sphere(5);
    private final CoordinateSystem3D coordinateSystem3D = new CylinderCoordinateSystem3D();

    public VisiblePointLight() {
        this(Color.WHITE);
    }

    public VisiblePointLight(Color color) {
        super(new Sphere(10));
        pointLight = new PointLight();
        material.setDiffuseColor(Color.BLACK);
        material.setSpecularColor(Color.TRANSPARENT);
        pointLight.colorProperty().addListener((o, c, n) -> setVisibleLightColor(n));
        setVisibleLightColor(color);
        pointLight.setColor(color);
        body.setMaterial(material);
        visiblePivot.visibleProperty().bind(body.visibleProperty());
        visiblePivot.setMaterial(new PhongMaterial(Color.BLACK));
        coordinateSystem3D.setVisible(false);
        getChildren().addAll(pointLight, coordinateSystem3D, visiblePivot);
    }

    public void bindPivotSphereToPivot(Translate translate) {
        visiblePivot.translateXProperty().bind(translate.xProperty().multiply(-1));
        visiblePivot.translateYProperty().bind(translate.yProperty().multiply(-1));
        visiblePivot.translateZProperty().bind(translate.zProperty().multiply(-1));
    }

    private void makeConstantImage(WritableImage img, Color color) {
        int width = (int) img.getWidth();
        ByteBuffer scanline = ByteBuffer.allocate(3 * width);
        byte r = (byte) ((int) Math.round(color.getRed() * 255.0));
        byte g = (byte) ((int) Math.round(color.getGreen() * 255.0));
        byte b = (byte) ((int) Math.round(color.getBlue() * 255.0));
        for (int i = 0; i < width; i++) {
            scanline.put(r);
            scanline.put(g);
            scanline.put(b);
        }
        scanline.rewind();
        img.getPixelWriter().setPixels(0, 0, width, width, PixelFormat.getByteRgbInstance(), scanline, 0);
    }

    final void setVisibleLightColor(Color color) {
        WritableImage selfIlluminationMap = new WritableImage(64, 64);
        makeConstantImage(selfIlluminationMap, color);
        material.setSelfIlluminationMap(selfIlluminationMap);
    }

    final void addScope(Node node) {
        ObservableList<Node> scope = pointLight.getScope();
        if (scope.isEmpty()) {
            scope.add(body);
        }
        if (node != null && !scope.contains(node)) {
            scope.add(node);
        }
    }

    final void removeScope(Node node) {
        ObservableList<Node> scope = pointLight.getScope();
        if (node != null) {
            scope.remove(node);
        }
    }

    public BooleanProperty pointLightVisibleProperty() {
        return body.visibleProperty();
    }

    public ObjectProperty<Color> pointLightColorProperty() {
        return pointLight.colorProperty();
    }

    public BooleanProperty coordinateSystem3DVisibleProperty() {
        return coordinateSystem3D.visibleProperty();
    }

}
