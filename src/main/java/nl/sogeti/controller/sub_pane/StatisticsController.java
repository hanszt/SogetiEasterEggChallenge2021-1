package nl.sogeti.controller.sub_pane;

import nl.sogeti.controller.FXMLController;
import nl.sogeti.model.Egg;
import nl.sogeti.model.MovableCameraPlatform;
import nl.sogeti.service.IMouseControlService;
import nl.sogeti.service.IStatisticsService;
import nl.sogeti.service.MouseControlService;
import nl.sogeti.service.StatisticsService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Set;

import static nl.sogeti.model.AppConstants.FxmlFile.STATISTICS_PANE;
import static java.lang.String.format;

public class StatisticsController extends FXMLController {

    private static final Set<Integer> FIBONACCI_NUMBER_SET = Set.of(1, 2, 3, 5, 8, 13, 21, 34, 55, 89);
    private final IStatisticsService statisticsService = new StatisticsService();

    @FXML
    private Label frameRateStatsLabel;
    @FXML
    private Label runTimeLabel;
    @FXML
    private Label animationIncrementLabel;
    @FXML
    private Label groupSizeLabel;
    @FXML
    private Label highlightingLabel;
    @FXML
    private Label turnFractionLabel;
    @FXML
    private Label distributionLabel;
    @FXML
    private Label positionXLabel;
    @FXML
    private Label positionYLabel;
    @FXML
    private Label positionZLabel;
    @FXML
    private Label rotationXLabel;
    @FXML
    private Label rotationYLabel;
    @FXML
    private Label positionCameraXLabel;
    @FXML
    private Label positionCameraYLabel;
    @FXML
    private Label positionCameraZLabel;
    @FXML
    private Label velocityCameraLabel;
    @FXML
    private Label fieldOfViewCameraLabel;

    public StatisticsController() throws IOException {
        super(STATISTICS_PANE.getFxmlFileName());
    }

    private static final String TWO_DEC_DOUBLE = "%-4.2f";

    public void updateGlobalStatistics(Duration runTimeSim, double animationIncrement) {
        frameRateStatsLabel.setText(format(TWO_DEC_DOUBLE + " f/s", statisticsService.getSimpleFrameRateMeter().getFrameRate()));
        runTimeLabel.setText(format("%-4.3f seconds", runTimeSim.toSeconds()));
        animationIncrementLabel.setText(format("%-4.2e increment/s", animationIncrement));
    }

    public void updateGroupStatistics(Egg egg, IMouseControlService iMouseControlService) {
        groupSizeLabel.setText(format("%d", egg.getMiniEggs().getChildren().size()));
        int highlightingValue = egg.getHighlightingValue();
        highlightingLabel.setText(format("%d", highlightingValue));
        if (FIBONACCI_NUMBER_SET.contains(highlightingValue)) highlightingLabel.setStyle("-fx-text-fill: red;");
        else highlightingLabel.setStyle(highlightingLabel.getScene().getRoot().getStyle());
        turnFractionLabel.setText(format("%4.7f", egg.getAngleFraction()));
        distributionLabel.setText(format("%4.3f", egg.getDistributionFactor()));

        MouseControlService mouseControlService = (MouseControlService) iMouseControlService;
        positionXLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateX()));
        positionYLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateY()));
        positionZLabel.setText(format(TWO_DEC_DOUBLE, mouseControlService.getTargetTranslateZ()));

        rotationXLabel.setText(format(TWO_DEC_DOUBLE + " deg", mouseControlService.getAngleX()));
        rotationYLabel.setText(format(TWO_DEC_DOUBLE + " deg", mouseControlService.getAngleY()));
    }

    public void updateCameraPlatformStatistics(MovableCameraPlatform cameraPlatform) {
        positionCameraXLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateX()));
        positionCameraYLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateY()));
        positionCameraZLabel.setText(format(TWO_DEC_DOUBLE, cameraPlatform.getTranslateZ()));

        velocityCameraLabel.setText(format(TWO_DEC_DOUBLE + " /s", cameraPlatform.getVelocity()));
        fieldOfViewCameraLabel.setText(format(TWO_DEC_DOUBLE + " deg", cameraPlatform.getCamera().getFieldOfView()));
    }

    protected StatisticsController getPojo() {
        return this;
    }

}
