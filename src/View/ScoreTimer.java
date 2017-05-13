package View;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by willi on 13/05/2017.
 */
public class ScoreTimer extends Label {
    private final static DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");

    private Duration time = Duration.ZERO;
    private DoubleProperty formattedTime = new SimpleDoubleProperty();
    private Timeline timeline;
    private long timeScore;

    public ScoreTimer() {
        super();

        setText("0:00");
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
                    Duration dur = ((KeyFrame)event.getSource()).getTime();
                    time = time.add(dur);
                    formattedTime.set(time.toMillis());
                    timeScore = formattedTime.longValue();
                    setTimeLbl(timeScore);
                }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void stopTimer() {
        timeline.stop();
    }

    public void setTimeLbl(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        setText(TIME_FORMAT.format(cal.getTime()));
    }

    public long getTime() {
        return timeScore;
    }

}
