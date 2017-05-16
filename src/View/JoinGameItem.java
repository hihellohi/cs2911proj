package View;

import Model.Netcode.RemoteMapModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Kevin Ni
 */
public class JoinGameItem extends ListCell<RemoteMapModel> {
    private HBox hBox;
    private Label ipLabel;
    private RemoteMapModel currentModel;
    private Button joinButton;
    private Button leaveButton;

    public JoinGameItem(){
        super();

        super.setPrefHeight(30);

        hBox = new HBox();
        ipLabel = new Label();
        currentModel = null;

        Pane pane = new Pane();
        joinButton = new Button("Join");
        leaveButton = new Button("Leave");

        //TODO on connection close change the thingy

        joinButton.setOnAction((e) -> {
            try {
                currentModel.connect();
                hBox.getChildren().set(2, leaveButton);
            }
            catch(IOException ex){
                currentModel = null;
                hBox.getChildren().get(2).setVisible(false);
                ((Label)hBox.getChildren().get(0)).setText("Unable to connect to host :(");
            }
        });

        leaveButton.setOnAction((e) -> {
            currentModel.close();
            hBox.getChildren().set(2, joinButton);
        });

        super.setText(null);

        hBox.getChildren().addAll(ipLabel, pane, joinButton);
        HBox.setHgrow(pane, Priority.ALWAYS);
    }

    @Override protected void updateItem(RemoteMapModel model, boolean empty){
        super.updateItem(model, empty);

        if(empty || model == null){
            currentModel = null;
            Platform.runLater(() ->{
                super.setGraphic(null);
            });
        }
        else{
            currentModel = model;

            if(model != null) {
                hBox.getChildren().get(2).setVisible(true);
                ipLabel.setText(currentModel.getHostName());
                if (model.isConnected()) {
                    hBox.getChildren().set(2, leaveButton);
                } else {
                    hBox.getChildren().set(2, joinButton);
                }
            }
            else{
                hBox.getChildren().get(2).setVisible(false);
                ((TextField)hBox.getChildren().get(0)).setText("Unable to connect :(");
            }

            Platform.runLater(() ->{
                super.setGraphic(hBox);
            });
        }
    }
}

