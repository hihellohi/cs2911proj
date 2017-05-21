package View;

import Model.Netcode.RemoteMapModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

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

        joinButton.setOnAction(this::onJoin);

        leaveButton.setOnAction((e) -> {
            currentModel.close();
            onLeave();
        });

        super.setText(null);

        hBox.getChildren().addAll(ipLabel, pane, joinButton);
        HBox.setHgrow(pane, Priority.ALWAYS);
    }

    private void onLeave(){
        Platform.runLater(() -> hBox.getChildren().set(2, joinButton));
        currentModel.setConnectionInterruptedListener(null);
    }

    private void onJoin(ActionEvent e){
        try {
            currentModel.connect();
            hBox.getChildren().set(2, leaveButton);
            currentModel.setConnectionInterruptedListener(() -> {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            String.format("You have been kicked from %s", currentModel.getHostName()));
                    alert.showAndWait();
                });
                onLeave();
            });
        }
        catch(IOException ex){
            currentModel = null;
            hBox.getChildren().get(2).setVisible(false);
            ((Label)hBox.getChildren().get(0)).setText("Unable to connect to host :(");
        }
    }

    @Override protected void updateItem(RemoteMapModel model, boolean empty){
        super.updateItem(model, empty);

        if(empty || model == null){
            currentModel = null;
            super.setGraphic(null);
        }
        else{
            currentModel = model;

            hBox.getChildren().get(2).setVisible(true);
            ipLabel.setText(currentModel.getHostName());
            if (model.isConnected()) {
                hBox.getChildren().set(2, leaveButton);
                currentModel.setConnectionInterruptedListener(this::onLeave);
            } else {
                hBox.getChildren().set(2, joinButton);
                currentModel.setConnectionInterruptedListener(null);
            }

            super.setGraphic(hBox);
        }
    }
}

