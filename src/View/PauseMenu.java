package View;

import Model.MapModel;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Created by willi on 16/05/2017.
 */
public class PauseMenu extends Dialog {
    private final static int BUTTON_WIDTH = 200;
    private final static int DIALOG_GAP = 10;
    private Window window;
    private Stage stage;

    public PauseMenu(MapModel model) {
        super();
        super.setTitle("Paused");

        VBox vbox = new VBox();

        window = super.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());

        Button returnToGame = new Button("Return to Game");
        returnToGame.setPrefWidth(BUTTON_WIDTH);
        returnToGame.setOnAction(event -> {
            window.hide();
        });
        returnToGame.setOnKeyPressed(hideWindow);

        Button mainMenu = new Button("Return to Main Menu");
        mainMenu.setPrefWidth(BUTTON_WIDTH);
        mainMenu.setOnAction(event -> {
            try {
                //TODO graceful exit notify clientconnection
                new UIController().switchHere(stage);
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
            window.hide();
        });
        mainMenu.setOnKeyPressed(hideWindow);

        Button newGame = new Button("New Game");
        newGame.setPrefWidth(BUTTON_WIDTH);
        newGame.setOnAction(event -> {
            model.generateNewMap();
            window.hide();
        });
        newGame.setOnKeyPressed(hideWindow);

        Button restart = new Button("Restart Game");
        restart.setPrefWidth(BUTTON_WIDTH);
        restart.setOnAction(event -> {
            model.reset();
            window.hide();
        });
        restart.setOnKeyPressed(hideWindow);

        Button exit = new Button("Exit");
        exit.setPrefWidth(BUTTON_WIDTH);
        exit.setOnAction(event -> {
            Platform.exit();
        });
        exit.setOnKeyPressed(hideWindow);

        vbox.getChildren().addAll(returnToGame, mainMenu, newGame, restart, exit);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(DIALOG_GAP);
        super.getDialogPane().setContent(vbox);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private EventHandler<KeyEvent> hideWindow = (e) -> {
        KeyCode code = e.getCode();
        if(code == KeyCode.P || code == KeyCode.ESCAPE){
            window.hide();
        }
    };
}
