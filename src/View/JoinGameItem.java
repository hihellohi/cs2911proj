package View;

import Model.Netcode.HostConnection;
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
import java.util.function.Consumer;

/**
 * @author Kevin Ni
 */
public class JoinGameItem extends ListCell<HostConnection> {
    private HBox hBox;
    private Label ipLabel;
    private HostConnection currentModel;
    private Button joinButton;
    private Button leaveButton;
    private Consumer<HostConnection> removeConnection;

    public JoinGameItem(Consumer<HostConnection> removeConnection){
        super();

        super.setPrefHeight(30);

        this.removeConnection = removeConnection;

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
        new Thread(() -> {
            try {
                currentModel.connect();
                Platform.runLater(() -> hBox.getChildren().set(2, leaveButton));
                currentModel.setConnectionInterruptedListener(() -> {
                    //on kick
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                String.format("You have been kicked from %s", currentModel.getHostName()));
                        alert.setHeaderText(null);
                        alert.showAndWait();
                    });
                    onLeave();
                });
            } catch (IOException ex) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Unable to connect to host :(");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                });
                removeConnection.accept(currentModel);
            }
        }).start();
    }

    @Override protected void updateItem(HostConnection model, boolean empty){
        super.updateItem(model, empty);

        if(empty || model == null){
            currentModel = null;
            Platform.runLater(() -> super.setGraphic(null));
        }
        else{
            currentModel = model;

            ipLabel.setText(currentModel.getHostName());
            if (model.isConnected()) {
                hBox.getChildren().set(2, leaveButton);
                currentModel.setConnectionInterruptedListener(this::onLeave);
            } else {
                hBox.getChildren().set(2, joinButton);
                currentModel.setConnectionInterruptedListener(null);
            }

            Platform.runLater(() -> super.setGraphic(hBox));
        }
    }
}

