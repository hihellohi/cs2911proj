package View;

import Model.LocalMapModel;
import Model.MapModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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

    public PauseMenu(MapModel model, EventHandler<ActionEvent> leaveHandler, boolean tutorial) {
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

        Button mainMenu = new Button("Return to Main Menu");
        mainMenu.setPrefWidth(BUTTON_WIDTH);
        mainMenu.setOnAction(event -> {
            leaveHandler.handle(event);
            window.hide();
        });

        Button exit = new Button("Exit");
        exit.setPrefWidth(BUTTON_WIDTH);
        exit.setOnAction(event -> {
            leaveHandler.handle(event);
            Platform.exit();
        });

        if(model instanceof LocalMapModel) {
            Button newGame = new Button("New Game");
            LocalMapModel localModel = (LocalMapModel) model;
            if (!tutorial) {
                newGame.setPrefWidth(BUTTON_WIDTH);
                newGame.setOnAction(event -> {
                    localModel.generateNewMap();
                    window.hide();
                });
            }

            Button restart = new Button("Restart Game");
            restart.setPrefWidth(BUTTON_WIDTH);
            restart.setOnAction(event -> {
                localModel.reset();
                window.hide();
            });

            if (tutorial) {
                vbox.getChildren().addAll(returnToGame, mainMenu, restart, exit);
            }
            else {
                vbox.getChildren().addAll(returnToGame, mainMenu, newGame, restart, exit);
            }
        }
        else{
            vbox.getChildren().addAll(returnToGame, mainMenu, exit);
        }
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(DIALOG_GAP);
        vbox.setOnKeyPressed(this::hideWindow);
        super.getDialogPane().setContent(vbox);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void hideWindow (KeyEvent e){
        KeyCode code = e.getCode();
        if(code == KeyCode.P || code == KeyCode.ESCAPE){
            window.hide();
        }
    }
}
