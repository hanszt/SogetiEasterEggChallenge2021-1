package nl.sogeti.controller.sub_pane;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import nl.sogeti.controller.FXMLController;
import nl.sogeti.controller.SceneManager;
import nl.sogeti.controller.scene.MainSceneController;
import nl.sogeti.controller.scene.SceneController;
import nl.sogeti.model.AppConstants;
import nl.sogeti.model.Egg;
import nl.sogeti.model.VisiblePointLight;
import nl.sogeti.model.appearance.Resource;
import nl.sogeti.service.BackgroundService;
import nl.sogeti.service.IBackgroundService;
import nl.sogeti.service.IThemeService;
import nl.sogeti.service.ThemeService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static nl.sogeti.model.AppConstants.FxmlFile.APPEARANCE_PANE;
import static nl.sogeti.model.AppConstants.*;

public class AppearanceController extends FXMLController {

    public static final int CHANGE_MILLIS = 1000;

    @FXML
    private ToggleButton pointLightVisibleButton;
    @FXML
    private ToggleButton opacityStageButton;
    @FXML
    private ToggleButton showCoordinateSystemsButton;
    @FXML
    private ComboBox<Resource> backgroundCombobox;
    @FXML
    private ComboBox<Resource> themeCombobox;
    @FXML
    private ToggleButton fullScreenButton;
    @FXML
    private ColorPicker backgroundColorPicker;
    @FXML
    private ColorPicker highlightingColorPicker;
    @FXML
    private ColorPicker seedColorPicker;
    @FXML
    private ColorPicker eggLightColorPicker;
    @FXML
    private ColorPicker mainLightColorPicker;

    private final IThemeService themeService = new ThemeService();
    private final IBackgroundService backgroundService = new BackgroundService();
    private final MainSceneController mainSceneController;

    public AppearanceController(MainSceneController mainSceneController) throws IOException {
        super(APPEARANCE_PANE.getFxmlFileName());
        this.mainSceneController = mainSceneController;

    }

    public void setup(Egg egg, VisiblePointLight mainVisiblePointLight, VisiblePointLight eggVisiblePointLight) {
        reset();
        backgroundColorPicker.setValue(INIT_BG_COLOR);
        setBackgroundColor();
        configureComboBoxes();
        configureColorPickers(egg, eggVisiblePointLight, mainVisiblePointLight);
        eggVisiblePointLight.pointLightVisibleProperty().bind(pointLightVisibleButton.selectedProperty());
        mainVisiblePointLight.pointLightVisibleProperty().bind(pointLightVisibleButton.selectedProperty());
        configureStageControlButtons(mainSceneController.getSceneManager().getStage());
    }

    public void reset() {
        seedColorPicker.setValue(INIT_MINI_EGG_COLOR);
        highlightingColorPicker.setValue(INIT_HIGHLIGHTING_COLOR);
        eggLightColorPicker.setValue(INIT_EGG_POINT_LIGHT_COLOR);
        mainLightColorPicker.setValue(INIT_MAIN_LIGHT_COLOR);
        showCoordinateSystemsButton.setSelected(false);
        pointLightVisibleButton.setSelected(false);
    }

    public void configureColorPickers(Egg egg, VisiblePointLight visibleEggPointLight, VisiblePointLight mainVisiblePointLight) {
        backgroundColorPicker.valueProperty().addListener(this::changeBackgroundColor);
        seedColorPicker.getCustomColors().add(INIT_MINI_EGG_COLOR);
        highlightingColorPicker.getCustomColors().add(INIT_HIGHLIGHTING_COLOR);
        eggLightColorPicker.getCustomColors().add(INIT_EGG_POINT_LIGHT_COLOR);
        mainLightColorPicker.getCustomColors().add(INIT_MAIN_LIGHT_COLOR);

        configureGraduallyChangingColorPickerAction(mainVisiblePointLight.pointLightColorProperty(), mainLightColorPicker);
        configureGraduallyChangingColorPickerAction(visibleEggPointLight.pointLightColorProperty(), eggLightColorPicker);
        configureGraduallyChangingColorPickerAction(egg.miniEggColorProperty(), seedColorPicker);
        configureGraduallyChangingColorPickerAction(egg.highlightingColorProperty(), highlightingColorPicker);
    }

    private void configureGraduallyChangingColorPickerAction(ObjectProperty<Color> colorProperty, ColorPicker colorPicker) {
        colorProperty.set(colorPicker.getValue());
        colorPicker.valueProperty().addListener((o, c, n) -> graduallyChangeColor(colorProperty, n));
    }

    private void graduallyChangeColor(ObjectProperty<Color> colorProperty, Color to) {
        Color from = colorProperty.get();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(colorProperty, from)),
                new KeyFrame(Duration.millis(CHANGE_MILLIS), new KeyValue(colorProperty, to)));
        timeline.playFromStart();
    }

    private static final String INIT_THEME_FILE_NAME = AppConstants.getProperty("init_theme", "");
    private static final String INIT_BG_FILE_NAME = AppConstants.getProperty("init_background", "");

    private void configureComboBoxes() {
        configureThemeCombobox();

        configureBackgroundCombobox();
    }

    private void configureBackgroundCombobox() {
        Set<Resource> bgResources = backgroundService.getResources();
        bgResources.forEach(r -> backgroundCombobox.getItems().add(r));
        backgroundCombobox.setValue(bgResources.stream()
                .filter(e -> e.getFileName().equals(INIT_BG_FILE_NAME))
                .findFirst().orElse(BackgroundService.NO_PICTURE));
        backgroundComboBoxAction();
    }

    private void configureThemeCombobox() {
        Set<Resource> themes = themeService.getThemes();
        themes.forEach(theme -> themeCombobox.getItems().add(theme));
        themeService.currentThemeProperty().bind(themeCombobox.valueProperty());
        themeService.styleSheetProperty().addListener(this::changeStyle);
        themeCombobox.setValue(themes.stream()
                .filter(e -> e.getFileName().equals(INIT_THEME_FILE_NAME))
                .findFirst().orElse(IThemeService.DEFAULT_THEME));
    }

    public void changeStyle(ObservableValue<? extends String> o, String c, String newVal) {
        SceneManager sceneManager = mainSceneController.getSceneManager();
        Collection<SceneController> sceneControllers = sceneManager.getSceneControllerMap().values();
        for (SceneController sceneController : sceneControllers) {
            ObservableList<String> styleSheets = sceneController.getScene().getStylesheets();
            styleSheets.removeIf(filter -> !styleSheets.isEmpty());
            if (newVal != null) styleSheets.add(newVal);
        }
    }

    public void configureStageControlButtons(Stage stage) {
        fullScreenButton.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));
        stage.fullScreenProperty().addListener((o, c, n) -> fullScreenButton.setSelected(n));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, key -> setFullScreenWhenF11Pressed(stage, key));
        bindStageOpacityToOpacityButton(stage);
    }

    private void setFullScreenWhenF11Pressed(Stage stage, KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.F11) {
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    private void bindStageOpacityToOpacityButton(Stage stage) {
        opacityStageButton.selectedProperty().addListener((o, c, n) -> stage.setOpacity(TRUE.equals(n) ? STAGE_OPACITY : 1));
    }

    @FXML
    private void backgroundComboBoxAction() {
        String path = backgroundCombobox.getValue().getPathToResource();
        if (path != null && !path.isEmpty()) {
            InputStream inputStream = backgroundService.getClass().getResourceAsStream(path);
            Image image = new Image(inputStream);
            AnchorPane animationPane = mainSceneController.getAnimationPane();
            animationPane.setBackground(backGround(image, animationPane));
        } else setBackgroundColor();
    }

    @NotNull
    private Background backGround(Image image, AnchorPane animationPane) {
        return new Background(new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(animationPane.getWidth(), animationPane.getHeight(),
                        false, false, false, true)));
    }

    private void changeBackgroundColor(ObservableValue<? extends Color> o, Color cur, Color newColor) {
        graduallyChangeBgColor(mainSceneController.getAnimationPane(), cur, newColor);
        backgroundCombobox.setValue(BackgroundService.NO_PICTURE);
    }

    public void graduallyChangeBgColor(Pane pane, Color fromColor, Color toColor) {
        ObjectProperty<Color> color = new SimpleObjectProperty<>(fromColor);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(color, fromColor)),
                new KeyFrame(Duration.millis(CHANGE_MILLIS), new KeyValue(color, toColor)));

        color.addListener((o, c, n) ->
                pane.setBackground(new Background(new BackgroundFill(n, CornerRadii.EMPTY, Insets.EMPTY))));
        timeline.playFromStart();
    }

    private void setBackgroundColor() {
        mainSceneController.getAnimationPane().setBackground(new Background(
                new BackgroundFill(backgroundColorPicker.getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public BooleanProperty showCoordinateSystemButtonSelectedProperty() {
        return showCoordinateSystemsButton.selectedProperty();
    }

    @Override
    protected FXMLController getPojo() {
        return this;
    }


}
