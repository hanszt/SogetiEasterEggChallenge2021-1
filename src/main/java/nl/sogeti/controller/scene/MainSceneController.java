package nl.sogeti.controller.scene;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import nl.sogeti.controller.SceneManager;
import nl.sogeti.controller.sub_pane.AppearanceController;
import nl.sogeti.controller.sub_pane.LightPositioningController;
import nl.sogeti.controller.sub_pane.StatisticsController;
import nl.sogeti.model.*;
import nl.sogeti.service.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalTime;

import static nl.sogeti.model.AppConstants.*;
import static nl.sogeti.model.AppConstants.FxmlFile.ABOUT_SCENE;
import static nl.sogeti.model.AppConstants.FxmlFile.MAIN_SCENE;
import static nl.sogeti.model.TurnFraction.STRING_TURN_FRACTION_MAP;

public class MainSceneController extends SceneController {

    private static final Logger LOGGER = LogManager.getLogger(MainSceneController.class);

    @FXML
    public Group logoGroup;
    @FXML
    public Tab lightPositioningTab;
    @FXML
    private Tab appearanceTab;
    @FXML
    private Tab statisticsTab;
    @FXML
    private AnchorPane animationPane;
    @FXML
    private ComboBox<AnimationAttribute> attributeCombobox;
    @FXML
    public ComboBox<TurnFraction> turnFractionComboBox;
    @FXML
    private ToggleButton pauseButton;
    @FXML
    private Slider distributionSlider;
    @FXML
    private Slider numberOfObjectsSlider;
    @FXML
    private Slider miniEggSizeSlider;
    @FXML
    private Slider turnFractionSlider;
    @FXML
    private Slider highlightingSlider;
    @FXML
    private Slider sizeSlider;
    @FXML
    private Slider animationIncrementSlider;
    @FXML
    private Slider cameraMaxVelocitySlider;
    @FXML
    private Slider cameraFieldOfViewSlider;

    private final Egg egg = new Egg();
    private final Group eggRoot = new Group(egg.getMiniEggs());

    private final MovableCameraPlatform cameraPlatform = new MovableCameraPlatform();
    private final Group subSceneRoot = new Group(eggRoot);
    private final StatisticsController statisticsController = new StatisticsController();

    private final LightPositioningController lightPositioningController = new LightPositioningController(this);
    private final AppearanceController appearanceController = new AppearanceController(this);
    private final IAnimationService animationService = new AnimationService();

    private final IMouseControlService mouseControlService = new MouseControlService(eggRoot);

    public MainSceneController(SceneManager sceneManager) throws IOException {
        super(MAIN_SCENE.getFxmlFileName(), sceneManager);
        SubScene subScene3D = new SubScene(subSceneRoot, 0, 0, true, SceneAntialiasing.BALANCED);
        this.animationPane.getChildren().add(subScene3D);
        configureSubScene(subScene3D, animationPane);
        this.statisticsTab.setContent(statisticsController.getRoot());
        this.lightPositioningTab.setContent(lightPositioningController.getRoot());
        this.appearanceTab.setContent(appearanceController.getRoot());
    }

    @Override
    public void setup() {
        logoGroup.getChildren().add(getSogetiLogo());
        configureAnimationPane(animationPane);
        configureCameraPlatform(cameraPlatform);
        setInitControlValues();
        configureComboBoxes();
        configureSliders();
        configureEgg();
        final VisiblePointLight mainLight = buildMainLight();
        subSceneRoot.getChildren().add(mainLight);
        final VisiblePointLight visiblePointLight = addEggPointLightToEggRoot();
        appearanceController.setup(egg, mainLight, visiblePointLight);
        configureMouseControlService();
        addCoordinateSystem(eggRoot);
        addCoordinateSystem(subSceneRoot);
        animationService.addLoopHandlersToTimelines(true, animationLoopEventHandler(), this::updateStatisticsLoop);
    }

    @NotNull
    private VisiblePointLight addEggPointLightToEggRoot() {
        final VisiblePointLight visiblePointLight = new VisiblePointLight(INIT_EGG_POINT_LIGHT_COLOR);
        lightPositioningController.addVisibleLightPositioning(visiblePointLight);
        visiblePointLight.coordinateSystem3DVisibleProperty().bind(appearanceController.showCoordinateSystemButtonSelectedProperty());
        eggRoot.getChildren().add(visiblePointLight);
        return visiblePointLight;
    }

    private void configureMouseControlService() {
        mouseControlService.initMouseControl(animationPane);
        mouseControlService.setOrientation(INIT_ANGLE_X, INIT_ANGLE_Y);
        mouseControlService.setTargetTranslation(INIT_EGG_TRANSLATION);
    }

    private void addCoordinateSystem(Group eggRoot) {
        final CoordinateSystem3D eggCoordinateSystem3D = new CylinderCoordinateSystem3D();
        eggCoordinateSystem3D.visibleProperty().bind(appearanceController.showCoordinateSystemButtonSelectedProperty());
        eggRoot.getChildren().add(eggCoordinateSystem3D);
    }

    @NotNull
    private Group getSogetiLogo() {
        Group sogetiLogo = new SogetiLogo();
        sogetiLogo.setScaleX(.4);
        sogetiLogo.setScaleY(.4);
        return sogetiLogo;
    }

    private void configureCameraPlatform(MovableCameraPlatform cameraPlatform) {
        cameraPlatform.setTranslate(INIT_CAMERA_PLATFORM_TRANSLATION);
        cameraPlatform.addKeyControls(scene);

        cameraPlatform.getCamera().fieldOfViewProperty().bindBidirectional(cameraFieldOfViewSlider.valueProperty());
        cameraPlatform.maxVelocityProperty().bindBidirectional(cameraMaxVelocitySlider.valueProperty());
    }

    private void configureSubScene(SubScene subScene, Pane animationPane) {
        subScene.widthProperty().bind(animationPane.widthProperty());
        subScene.heightProperty().bind(animationPane.heightProperty());
        subScene.setCamera(cameraPlatform.getCamera());
    }

    private VisiblePointLight buildMainLight() {
        VisiblePointLight pointLight = new VisiblePointLight();
        pointLight.setTranslateZ(INIT_MAIN_LIGHT_TRANSLATION.getZ());
        pointLight.setTranslateY(INIT_MAIN_LIGHT_TRANSLATION.getY());
        return pointLight;
    }

    private void configureAnimationPane(AnchorPane animationPane) {
        animationPane.setPrefSize(INIT_ANIMATION_PANE_DIMENSION.getWidth(), INIT_ANIMATION_PANE_DIMENSION.getHeight());
        animationPane.setBackground(new Background(new BackgroundFill(INIT_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void configureComboBoxes() {
        attributeCombobox.getItems().addAll(turnFractionAttribute, highLightingAttribute, distributionAttribute, memberCountAttribute, none);
        attributeCombobox.setValue(memberCountAttribute);

        TurnFraction.configureComboBox(turnFractionComboBox);
        turnFractionComboBox.valueProperty().addListener(this::turnFractionComboboxValueChanged);
    }

    private void turnFractionComboboxValueChanged(ObservableValue<? extends TurnFraction> o,
                                                  TurnFraction c, TurnFraction n) {
        boolean parsable = n != null && (n.isParsable() || turnFractionComboBox.getItems().contains(n));
        if (parsable) {
            setTurnFractionSliderBounds(n);
            egg.angleFractionProperty().setValue(n.getValue());
            if (!turnFractionComboBox.getItems().contains(n)) turnFractionComboBox.getItems().add(n);
        }
    }

    private static final double TURN_FRACTION_SLIDER_RANGE =
            AppConstants.parsedDoubleProp("turn_fraction_slider_range", .02);

    private void setTurnFractionSliderBounds(TurnFraction n) {
        turnFractionSlider.setMin(n.getValue() - TURN_FRACTION_SLIDER_RANGE / 2);
        turnFractionSlider.setMax(n.getValue() + TURN_FRACTION_SLIDER_RANGE / 2);
    }

    private void configureEgg() {
        egg.angleFractionProperty().bindBidirectional(turnFractionSlider.valueProperty());
        egg.distributionFactorProperty().bind(distributionSlider.valueProperty());
        egg.groupRadiusProperty().bind(sizeSlider.valueProperty());
        egg.highlightingValueProperty().bind(highlightingSlider.valueProperty());
        egg.miniEggSizeProperty().bind(miniEggSizeSlider.valueProperty());
        egg.memberSizeProperty().bind(numberOfObjectsSlider.valueProperty());
        egg.animationAttributeProperty().set(attributeCombobox.getValue());
        egg.getAnimationAttribute().updateIncrementSliderBounds(animationIncrementSlider, ANIMATION_FACTOR);
    }

    private EventHandler<ActionEvent> animationLoopEventHandler() {
        return loop -> animationService.run(egg, cameraPlatform);
    }

    private void updateStatisticsLoop(ActionEvent loopEvent) {
        double animationIncrement = animationIncrementSlider.getValue() / TARGET_FRAME_RATE;
        Duration runTimeSim = Duration.millis((LocalTime.now().toNanoOfDay() - startTimeScene.toNanoOfDay()) / 1e6);
        statisticsController.updateGroupStatistics(egg, mouseControlService);
        statisticsController.updateGlobalStatistics(runTimeSim, animationIncrement);
        statisticsController.updateCameraPlatformStatistics(cameraPlatform);
    }

    private void configureSliders() {
        turnFractionSlider.valueProperty().addListener(this::updateEggPositions);
        setTurnFractionSliderBounds(turnFractionComboBox.getValue());
        distributionSlider.valueProperty().addListener(this::updateEggPositions);
        sizeSlider.valueProperty().addListener(this::updateEggPositions);
        highlightingSlider.valueProperty().addListener((o, c, n) -> egg.updateHighLighting());
    }

    private void updateEggPositions(ObservableValue<? extends Number> o, Number c, Number n) {
        egg.updatePositions();
    }

    private static final int MAX_NR_OF_SHAPES = parsedIntProp("max_number_of_shapes", 1000);

    private void setInitControlValues() {
        numberOfObjectsSlider.setMax(MAX_NR_OF_SHAPES);
        numberOfObjectsSlider.setValue(INIT_NUMBER_OF_SHAPES);
        miniEggSizeSlider.setValue(INIT_SEED_SIZE);
        distributionSlider.setValue(DISTRIBUTION_FACTOR);
        turnFractionSlider.setValue(AppConstants.GOLDEN_RATIO);
        sizeSlider.setValue(INIT_GROUP_RADIUS);
        highlightingSlider.setValue(INIT_HIGHLIGHTING_VALUE);
        cameraFieldOfViewSlider.setValue(INIT_CAMERA_FOV);
        cameraMaxVelocitySlider.setValue(INIT_CAMERA_VELOCITY);
    }

    @FXML
    private void pauseSimButtonAction() {
        if (pauseButton.isSelected()) {
            animationService.pauseAnimationTimeline();
        } else animationService.startAnimationTimeline();
    }

    @FXML
    private void resetButtonAction() {
        setInitControlValues();
        egg.reset();
        appearanceController.reset();
        attributeCombobox.setValue(memberCountAttribute);
        animatedAttributeComboboxAction();
        pauseButton.setSelected(false);
        turnFractionComboBox.setValue(STRING_TURN_FRACTION_MAP.get(TurnFraction.PHI));
        pauseSimButtonAction();
    }

    @FXML
    private void distributionButtonAction() {
        distributionSlider.setValue(DISTRIBUTION_FACTOR);
    }

    @FXML
    public void recenterButtonAction() {
        cameraPlatform.setTranslate(INIT_CAMERA_PLATFORM_TRANSLATION);
        cameraPlatform.getCamera().setFieldOfView(INIT_CAMERA_FOV);
        mouseControlService.setOrientation(INIT_ANGLE_X, INIT_ANGLE_Y);
        mouseControlService.setTargetTranslation(INIT_EGG_TRANSLATION);
        lightPositioningController.recenter();
    }

    @FXML
    private void openBrowserButtonAction(ActionEvent actionEvent) {
        Application application = new Application() {
            @Override
            public void start(Stage stage) {
                //This new instance is to refer to the browser
            }
        };
        application.getHostServices().showDocument(AppConstants.getProperty("my_repo", "https://gitlab.com/hanszt"));
        actionEvent.consume();
    }

    private static final double ANIMATION_FACTOR =
            AppConstants.parsedDoubleProp("animation_speed_factor", 1.);

    @FXML
    private void animatedAttributeComboboxAction() {
        egg.animationAttributeProperty().setValue(attributeCombobox.getValue());
        attributeCombobox.getValue().updateIncrementSliderBounds(animationIncrementSlider, ANIMATION_FACTOR);
    }

    @FXML
    private void showAbout() {
        sceneManager.setupScene(ABOUT_SCENE);
    }

    protected SceneController getPojo() {
        return this;
    }

    private final AnimationAttribute none = new AnimationAttribute(
            "None", 0, 0, 0) {
        @Override
        public void updateValue() {
            LOGGER.trace("No attribute selected...");
        }
    };

    private final AnimationAttribute turnFractionAttribute = new AnimationAttribute(
            "Turn fraction", -1e-5, 1e-5, 1e-6) {
        @Override
        public void updateValue() {
            double newVal = turnFractionSlider.getValue() + animationIncrementSlider.getValue();
            turnFractionSlider.setValue(newVal);
        }
    };

    private final AnimationAttribute distributionAttribute = new AnimationAttribute(
            "Distribution", -.01, .01, -0.005) {
        @Override
        public void updateValue() {
            distributionSlider.setValue(distributionSlider.getValue() + animationIncrementSlider.getValue());
        }
    };

    private final AnimationAttribute highLightingAttribute = new AnimationAttribute(
            "Highlighting", -.1, .1, .05) {
        @Override
        public void updateValue() {
            highlightingSlider.setValue(highlightingSlider.getValue() + animationIncrementSlider.getValue());
        }
    };

    private final AnimationAttribute memberCountAttribute = new AnimationAttribute(
            "Member count", -10, 10, 1) {
        @Override
        public void updateValue() {
            numberOfObjectsSlider.setValue(numberOfObjectsSlider.getValue() + animationIncrementSlider.getValue());
        }
    };

    public AnchorPane getAnimationPane() {
        return animationPane;
    }
}

