package nl.sogeti.service;

import nl.sogeti.model.MovableCameraPlatform;
import nl.sogeti.model.Egg;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface IAnimationService {

    void run(Egg egg, MovableCameraPlatform cameraPlatform);

    void addLoopHandlersToTimelines(boolean start,
                                    EventHandler<ActionEvent> animationLoop, EventHandler<ActionEvent> statisticsLoop);
    void pauseAnimationTimeline();

    void startAnimationTimeline();
}
