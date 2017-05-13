package View;

import Model.MapModel;
import Model.MapUpdateInfo;
import Model.ModelEventHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Created by willi on 13/05/2017.
 */
public class ScoreView extends BorderPane implements ModelEventHandler<MapUpdateInfo> {
    private final static int TILE_SIZE = 100;
    private final static int SIDE_PANEL_SIZE = 3;
    private Label scoreLbl;
    private ScoreTimer timeLbl;
    private MapModel model;
    private Button resetBtn;

    public ScoreView(MapModel model) {
        super();
        super.addEventHandler(KeyEvent.KEY_PRESSED, model);

        this.model = model ;
        model.subscribeModelUpdate(this);

        super.setPrefSize(sideWidth(), sideHeight());
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

    public void resetGame(ActionEvent actionEvent) {
    }

    @Override
    public void handle(MapUpdateInfo updateInfo) {
        if (updateInfo.isFinished())
            timeLbl.stopTimer();

        Platform.runLater(() -> scoreLbl.setText(String.valueOf(model.getScore())));
        model.setTime(timeLbl.getTime());
    }

    public int sideHeight() {
        return model.getHeight() * TILE_SIZE;

    }
    public int sideWidth() {
        return (model.getWidth() * TILE_SIZE) / SIDE_PANEL_SIZE;

    }
}
