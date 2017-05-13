package View;

import Model.MapModel;
import Model.MapUpdateInfo;
import Model.ModelEventHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

/**
 * Created by willi on 13/05/2017.
 */
public class ScoreTimer extends Label implements ModelEventHandler<MapUpdateInfo> {
    private Duration time = Duration.ZERO;
    private DoubleProperty formattedTime = new SimpleDoubleProperty();
    private Timeline timeline;
    private MapModel model;

    public ScoreTimer(MapModel model) {
        super();
        super.addEventHandler(KeyEvent.KEY_PRESSED, model);

        this.model = model;
        model.subscribeModelUpdate(this);

        setText("0:00");
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Duration dur = ((KeyFrame)event.getSource()).getTime();
                                time = time.add(dur);
                                formattedTime.set(time.toMillis());
                                model.setTime(formattedTime.longValue());
                                setText(model.getTime());
                            }
                        })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void handle(MapUpdateInfo updateInfo) {
        if (updateInfo.isFinished())
            timeline.stop();
    }
}
