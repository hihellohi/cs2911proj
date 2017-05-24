package View;

import Model.MapModel;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Created by willi on 16/05/2017.
 */
public class EndGameDialog extends Dialog {
    private final static int DIALOG_WIDTH = 200;
    private final static int DIALOG_HEIGHT = 70;
    private final static int DIALOG_GAP = 20;

    public EndGameDialog(ScoreView sv) {
        super();
        super.setTitle("Congratulations!");
        super.getDialogPane().setPrefSize(DIALOG_WIDTH, DIALOG_HEIGHT);

        // closeAndRemoveFromModel dialog
        Window window = super.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());

        VBox vbox= new VBox();
        HBox hboxScore = new HBox();
        HBox hboxTime = new HBox();

        Label scoreHeader = new Label("SCORE: ");
        Label scoreLbl = new Label(String.valueOf(sv.getScore()));
        hboxScore.getChildren().addAll(scoreHeader, scoreLbl);
        hboxScore.setAlignment(Pos.CENTER);

        Label timeHeader = new Label("TIME: ");
        Label timeLbl = new Label(sv.getTime());
        hboxTime.getChildren().addAll(timeHeader, timeLbl);
        hboxTime.setAlignment(Pos.CENTER);

        Button ok = new Button("OK");
        ok.setOnAction(event -> window.hide());

        vbox.getChildren().addAll(hboxScore, hboxTime, ok);
        vbox.setSpacing(DIALOG_GAP);
        vbox.setAlignment(Pos.CENTER);
        super.getDialogPane().setContent(vbox);
    }
}
