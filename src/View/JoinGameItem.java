package View;

import Model.Netcode.ClientConnection;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * @author Kevin Ni
 */
public class JoinGameItem extends ListCell<String> {
    private HBox hBox;
    private Label ipLabel;
    private String currentString;

    public JoinGameItem(){
        super();

        super.setPrefHeight(30);

        hBox = new HBox();
        ipLabel = new Label();
        currentString = null;

        Pane pane = new Pane();
        Button button = new Button("Join");

        super.setText(null);

        hBox.getChildren().addAll(ipLabel, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setOnAction((e)->{
            System.out.println(currentString);
        });
    }

    @Override protected void updateItem(String string, boolean empty){
        super.updateItem(string, empty);

        if(empty || string == null){
            currentString = null;
            Platform.runLater(() ->{
                super.setGraphic(null);
            });
        }
        else{
            currentString = string;
            ipLabel.setText(string);
            Platform.runLater(() ->{
                super.setGraphic(hBox);
            });
        }
    }
}

