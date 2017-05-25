package View;

import Model.MapModel;
import Model.MapUpdateInfo;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Class to keep track of and display the current score.
 *
 */
public class ScoreView extends BorderPane {
    private final static int SIDE_PANEL_SIZE = 150;
    private Label scoreLbl;
    private ScoreTimer timeLbl;
    private int score;

    /**
     * Initialise score and timer, and setup labels.
     *
     */
    public ScoreView() {
        super();
        score = 0;

        super.setPrefWidth(sideWidth());
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        // movement score
        Label scoreHeader = new Label("SCORE");
        scoreLbl = new Label("0");
        vbox.getChildren().addAll(scoreHeader, scoreLbl);

        // time score
        Label timeHeader = new Label("TIME");
        timeLbl = new ScoreTimer();
        vbox.getChildren().addAll(timeHeader, timeLbl);

        // add everything
        super.setTop(vbox);
    }

    /**
     * Update the score when certain parameters are met.
     *
     * @param updateInfo
     * @pre updateInfo != null
     */
    public void onMapChange(MapUpdateInfo updateInfo){
        if (updateInfo.isFinished()) {
            timeLbl.stopTimer();
        }
        else if (updateInfo.isNewMap()) {
            score = 0;
            timeLbl.resetTimer();
        }
        else if (updateInfo.size() > 1){
            score++;
        }
        Platform.runLater(() -> scoreLbl.setText(String.valueOf(score)));
    };

    /**
     * Return the current score.
     *
     * @return score >= 0
     */
    public int getScore() {
        return score;
    }

    /**
     * Return the current time as a string.
     *
     * @return time >= 0:00
     */
    public String getTime() {
        return timeLbl.timeToString();
    }

    /**
     * Return the width of the ScoreView.
     *
     * @return SIDE_PANEL_SIZE
     */
    public int sideWidth() {
        return SIDE_PANEL_SIZE;
    }
}
