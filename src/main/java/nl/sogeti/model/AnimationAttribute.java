package nl.sogeti.model;

import javafx.scene.control.Slider;

public abstract class AnimationAttribute {

    private final String name;
    private final double minValue;
    private final double maxValue;
    private final double initValue;

   protected AnimationAttribute(String name, double minValue, double maxValue, double initValue) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.initValue = initValue;
    }

    public abstract void updateValue();

    // first set the min and max value, after that the current value
    public void updateIncrementSliderBounds(Slider slider, double speedFactor) {
        slider.setMin(minValue * speedFactor);
        slider.setMax(maxValue * speedFactor);
        slider.setValue(initValue);
    }

    public String toString() {
        return name;
    }

}
