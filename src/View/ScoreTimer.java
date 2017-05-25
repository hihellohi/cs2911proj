package View;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class to control the timer in the ScoreView.
 *
 */
public class ScoreTimer extends Label {
    private final static DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");

    private Duration time;
    private DoubleProperty formattedTime = new SimpleDoubleProperty();
    private Timeline timeline;
    private long timeScore;

    /**
     * Initialise the timer to 0s and start the timer.
     *
     */
    public ScoreTimer() {
        super();
        initTimer();
        startTimer();
    }

    /**
     * Initialise the timer to 0s.
     */
    public void initTimer() {
        time = Duration.ZERO;
        timeScore = 0;
        Platform.runLater(() -> setText("0:00"));
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
                    Duration dur = ((KeyFrame)event.getSource()).getTime();
                    time = time.add(dur);
                    formattedTime.set(time.toMillis());
                    timeScore = formattedTime.longValue();
                    Platform.runLater(() -> setText(timeToString()));
                }
                ));
    }

    /**
     * Reset the timer back to 0s, and start timing again.
     *
     */
    public void resetTimer() {
        stopTimer();
        initTimer();
        startTimer();
    }

    /**
     * Start the timer.
     *
     */
    public void startTimer() {
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Stop the timer.
     *
     */
    public void stopTimer() {
        timeline.stop();
    }

    /**
     * Convert the current time to string format - "mm:ss".
     *
     * @return time in "mm:ss"
     */
    public String timeToString() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeScore);
        return TIME_FORMAT.format(cal.getTime());
    }
}
