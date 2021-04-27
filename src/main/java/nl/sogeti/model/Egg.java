package nl.sogeti.model;

import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import static nl.sogeti.model.AppConstants.INIT_EGG_FACTOR;

public class Egg implements Iterable<MiniEgg> {

    private final Group miniEggs = new Group();

    private final IntegerProperty highlightingValue = new SimpleIntegerProperty();

    private final IntegerProperty memberSize = new SimpleIntegerProperty();
    private final DoubleProperty angleFraction = new SimpleDoubleProperty();
    private final DoubleProperty distributionFactor = new SimpleDoubleProperty();
    private final DoubleProperty miniEggSize = new SimpleDoubleProperty();
    private final DoubleProperty eggFactor = new SimpleDoubleProperty(INIT_EGG_FACTOR);
    private final DoubleProperty groupRadius = new SimpleDoubleProperty();

    private final ObjectProperty<Color> miniEggColor = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> highlightingColor = new SimpleObjectProperty<>();
    private final ObjectProperty<AnimationAttribute> animationAttribute = new SimpleObjectProperty<>();

    public Egg() {
        super();
        memberSize.addListener((o, c, n) -> controlMembersSize(n.intValue()));
        miniEggs.getChildren().addListener(this::onMiniEggAmountChanged);
    }

    private void onMiniEggAmountChanged(ListChangeListener.Change<? extends Node> change) {
        int index = 0;
        for (Node node : change.getList()) {
            setHighlighting((MiniEgg) node, index);
            index++;
        }
    }

    public void controlMembersSize(int numberOfBalls) {
        while (miniEggs.getChildren().size() != numberOfBalls) {
            if (miniEggs.getChildren().size() < numberOfBalls) addSmartShapeToGroup();
            else removeShape();
        }
        updatePositions();
    }

    private void addSmartShapeToGroup() {
        MiniEgg shape = createSmartShape(miniEggSize);
        shape.diffuseColorProperty().bind(miniEggColor);
        miniEggs.getChildren().add(shape);
    }

    private MiniEgg createSmartShape(DoubleProperty shapeSize) {
        Sphere sphere = new Sphere();
        sphere.scaleYProperty().bind(eggFactor);
        sphere.radiusProperty().bind(shapeSize);
        return new MiniEgg(sphere);
    }

    public void updatePositions() {
        int index = 0;
        for (MiniEgg child : this) {
            setPosition(child, index, getMiniEggs().getChildren().size());
            index++;
        }
    }

    public void updateHighLighting() {
        int index = 0;
        for (MiniEgg child : this) {
            setHighlighting(child, index);
            index++;
        }
    }

    public void loopUpdate() {
        animationAttribute.get().updateValue();
    }

    public void setHighlighting(MiniEgg child, int counter) {
        if (highlightingValue.get() != 0 && counter % highlightingValue.get() == 0) {
            child.diffuseColorProperty().bind(highlightingColor);
        } else {
            child.diffuseColorProperty().bind(miniEggColor);
        }
    }

    public void setPosition(MiniEgg shape, int index, int numberOfBalls) {
        double normalizedIndex = (double) index / (numberOfBalls - 1);
        double radius = Math.pow(normalizedIndex, distributionFactor.get()) * groupRadius.get();
        double theta = 2 * Math.PI * angleFraction.get() * index;
        double phi = Math.acos(1 - (2 * normalizedIndex));
        double x = radius * Math.cos(theta) * Math.sin(phi);
        double y = radius * (1 - (2 * normalizedIndex)) * (index > numberOfBalls / 2. ? eggFactor.get() : 1);
        double z = radius * Math.sin(theta) * Math.sin(phi);
        shape.setTranslateX(x);
        shape.setTranslateY(y);
        shape.setTranslateZ(z);
    }

    private void removeShape() {
        ObservableList<Node> list = miniEggs.getChildren();
        MiniEgg shape = (MiniEgg) list.get(0);
        miniEggs.getChildren().remove(shape);
    }

    public void reset() {
    }

    @NotNull
    @Override
    public Iterator<MiniEgg> iterator() {
        return miniEggs.getChildren().stream().map(MiniEgg.class::cast).iterator();
    }

    public double getAngleFraction() {
        return angleFraction.get();
    }

    public IntegerProperty memberSizeProperty() {
        return memberSize;
    }

    public DoubleProperty angleFractionProperty() {
        return angleFraction;
    }

    public double getDistributionFactor() {
        return distributionFactor.get();
    }

    public DoubleProperty distributionFactorProperty() {
        return distributionFactor;
    }

    public DoubleProperty groupRadiusProperty() {
        return groupRadius;
    }

    public DoubleProperty miniEggSizeProperty() {
        return miniEggSize;
    }

    public ObjectProperty<Color> miniEggColorProperty() {
        return miniEggColor;
    }

    public ObjectProperty<Color> highlightingColorProperty() {
        return highlightingColor;
    }

    public ObjectProperty<AnimationAttribute> animationAttributeProperty() {
        return animationAttribute;
    }

    public AnimationAttribute getAnimationAttribute() {
        return animationAttribute.get();
    }

    public int getHighlightingValue() {
        return highlightingValue.get();
    }

    public IntegerProperty highlightingValueProperty() {
        return highlightingValue;
    }

    public Group getMiniEggs() {
        return miniEggs;
    }

}
