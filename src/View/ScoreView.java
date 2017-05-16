package View;

import Model.MapModel;
import Model.MapUpdateInfo;
import Model.ModelEventHandler;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Created by willi on 13/05/2017.
 */
public class ScoreView extends BorderPane implements ModelEventHandler<MapUpdateInfo> {
    private final static int SIDE_PANEL_SIZE = 150;
    private Label scoreLbl;
    private ScoreTimer timeLbl;
    private MapModel model;
    private int score;

    public ScoreView(MapModel model) {
        super();
        super.addEventHandler(KeyEvent.KEY_PRESSED, model);

        this.model = model;
        score = 0;
        model.subscribeModelUpdate(this);

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

    @Override
    public void handle(MapUpdateInfo updateInfo) {
        if (updateInfo.isFinished()) {
            timeLbl.stopTimer();
        }
        else if (updateInfo.isNewMap()) {
            score = 0;
            timeLbl.resetTimer();
        }
        else {
            score++;
        }
        Platform.runLater(() -> scoreLbl.setText(String.valueOf(score)));
    }

    public int getScore() {
        return score;
    }

    public String getTime() {
        return timeLbl.timeToString();
    }

    public int sideWidth() {
        return SIDE_PANEL_SIZE;
    }
}
