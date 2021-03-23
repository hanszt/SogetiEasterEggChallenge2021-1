package nl.sogeti.service;

import nl.sogeti.model.MovableCameraPlatform;
import nl.sogeti.model.Egg;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import static nl.sogeti.model.AppConstants.TARGET_FRAME_DURATION;
import static javafx.animation.Animation.INDEFINITE;

public class AnimationService implements IAnimationService {

    private final Timeline animationTimeline;
    private final Timeline statisticsTimeline;

    public AnimationService() {
        this.animationTimeline = setupTimeline();
        this.statisticsTimeline = setupTimeline();
    }

   private Timeline setupTimeline() {
        Timeline t = new Timeline();
        t.setCycleCount(INDEFINITE);
        return t;
    }

    public void addLoopHandlersToTimelines(boolean start,
                                           EventHandler<ActionEvent> animationLoop, EventHandler<ActionEvent> statisticsLoop) {
        animationTimeline.getKeyFrames().add(new KeyFrame(TARGET_FRAME_DURATION, "Animation keyframe", animationLoop));
        statisticsTimeline.getKeyFrames().add(new KeyFrame(TARGET_FRAME_DURATION, "Statistics keyframe", statisticsLoop));
        if (start) {
            animationTimeline.play();
            statisticsTimeline.play();
        }
    }

    public void run(Egg egg, MovableCameraPlatform cameraPlatform) {
        egg.loopUpdate();
        cameraPlatform.update(animationTimeline.getCycleDuration());
    }

    public void startAnimationTimeline() {
        animationTimeline.play();
    }

    public void pauseAnimationTimeline() {
        animationTimeline.pause();
    }

}
