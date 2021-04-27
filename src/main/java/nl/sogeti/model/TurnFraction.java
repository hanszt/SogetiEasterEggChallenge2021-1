package nl.sogeti.model;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.util.Map;
import java.util.Objects;

public class TurnFraction {

    static final String DIGITS = "(\\p{Digit}+)";
    static final String HEX_DIGITS = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    static final String EXP = "[eE][+-]?" + DIGITS;
    private static final String DOUBLE_REGEX =
            "[\\x00-\\x20]*" +  // Optional leading "whitespace"
                    "[+-]?(" + // Optional sign character
                    "NaN|" +           // "NaN" string
                    "Infinity|" +      // "Infinity" string

                    // A decimal floating-point string representing a finite positive
                    // number without a leading sign has at most five basic pieces:
                    // Digits . Digits ExponentPart FloatTypeSuffix
                    //
                    // Since this method allows integer-only strings as input
                    // in addition to strings of floating-point literals, the
                    // two sub-patterns below are simplifications of the grammar
                    // productions from section 3.10.2 of
                    // The Java Language Specification.

                    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                    "(((" + DIGITS + "(\\.)?(" + DIGITS + "?)(" + EXP + ")?)|" +

                    // . Digits ExponentPart_opt FloatTypeSuffix_opt
                    "(\\." + DIGITS + "(" + EXP + ")?)|" +

                    // Hexadecimal strings
                    "((" +
                    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HEX_DIGITS + "(\\.)?)|" +

                    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                    "(0[xX]" + HEX_DIGITS + "?(\\.)" + HEX_DIGITS + ")" +

                    ")[pP][+-]?" + DIGITS + "))" +
                    "[fFdD]?))" +
                    "[\\x00-\\x20]*";// Optional trailing "whitespace"


    public static final String PHI = "golden ratio";

    public static final Map<String, TurnFraction> STRING_TURN_FRACTION_MAP = Map.of(
            PHI, new TurnFraction(PHI, AppConstants.GOLDEN_RATIO),
            "square root of two", new TurnFraction("Square root of two", Math.sqrt(2)),
            "pi", new TurnFraction("Pi", Math.PI),
            "1/pi", new TurnFraction("1/pi", 1 / Math.PI),
            "e", new TurnFraction("e", Math.E));

    private final String name;
    private final double value;
    private boolean parsable = true;

    public TurnFraction(String value) {
        this.name = value;
        this.value = parseValue(value);
    }

    public TurnFraction(String name, double value) {
        this.name = name;
        this.value = value;
    }

    private static final String FRACTION_REGEX = DOUBLE_REGEX + "/" + DOUBLE_REGEX;

    private double parseValue(String value) {
        if (value.matches(DOUBLE_REGEX)) return Double.parseDouble(value);
        else if (value.matches(FRACTION_REGEX)) return parseFraction(value);
        parsable = false;
        return 0;
    }

    private double parseFraction(String value) {
        String[] values = value.strip().split("/");
        return Double.parseDouble(values[0].strip()) / Double.parseDouble(values[1].strip());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TurnFraction that = (TurnFraction) o;
        return Double.compare(that.value, value) == 0 && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    public String formattedName() {
        return !name.isEmpty() ?
                name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase().replace("_", " ")
                : "";
    }

    public double getValue() {
        return value;
    }

    public boolean isParsable() {
        return parsable;
    }

    public static void configureComboBox(ComboBox<TurnFraction> turnFractionComboBox) {
        STRING_TURN_FRACTION_MAP.forEach((key, value) -> turnFractionComboBox.getItems().add(value));
        turnFractionComboBox.getItems().addAll(
                new TurnFraction("113/355"),
                new TurnFraction("7/22"),
                new TurnFraction("1/12"),
                new TurnFraction("1/10"),
                new TurnFraction("1/6"),
                new TurnFraction("1/5"),
                new TurnFraction("1/4"),
                new TurnFraction("1/3"),
                new TurnFraction("1/2"),
                new TurnFraction("1"));

        turnFractionComboBox.setValue(STRING_TURN_FRACTION_MAP.get(PHI));
        turnFractionComboBox.setConverter(TurnFraction.getStringConverter());

        final TextField textField = turnFractionComboBox.getEditor();
        textField.setOnMousePressed(e -> textField.clear());
        textField.textProperty().addListener((o, c, value) -> highLightWhenNotParsable(textField, value));
    }

    private static void highLightWhenNotParsable(TextField textField, String value) {
        boolean parsable = STRING_TURN_FRACTION_MAP.containsKey(value.toLowerCase()) ||
                value.matches(DOUBLE_REGEX) ||
                value.matches(FRACTION_REGEX);

        if (parsable) textField.setStyle("-fx-text-fill: black;");
        else textField.setStyle("-fx-text-fill: red;");
    }

    public static StringConverter<TurnFraction> getStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(TurnFraction turnFraction) {
                return turnFraction.formattedName();
            }

            @Override
            public TurnFraction fromString(String input) {
                String toLower = input.toLowerCase();
                if (STRING_TURN_FRACTION_MAP.containsKey(toLower)) {
                    return STRING_TURN_FRACTION_MAP.get(toLower);
                }
                return new TurnFraction(toLower);
            }
        };
    }

    @Override
    public String toString() {
        return String.format("TurnFraction{name='%s', value=%s, parsable=%s}", name, value, parsable);
    }
}
