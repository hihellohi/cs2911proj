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
 * A cell in the listView in the join game scene
 * represents a discovered host
 *
 * @author Kevin Ni
 */
public class JoinGameItem extends ListCell<HostConnection> {
    private HBox hBox;
    private Label ipLabel;
    private HostConnection host;
    private Button joinButton;
    private Button leaveButton;
    private Consumer<HostConnection> removeConnection;

    /**
     * class constructor
     *
     * @param removeConnection callback that is invoked when
     */
    public JoinGameItem(Consumer<HostConnection> removeConnection){
        super();

        super.setPrefHeight(30);

        this.removeConnection = removeConnection;

        hBox = new HBox();
        ipLabel = new Label();
        host = null;

        Pane pane = new Pane();
        joinButton = new Button("Join");
        leaveButton = new Button("Leave");

        joinButton.setOnAction(this::onJoin);

        leaveButton.setOnAction((e) -> {
            host.close();
            onLeave();
        });

        super.setText(null);

        hBox.getChildren().addAll(ipLabel, pane, joinButton);
        HBox.setHgrow(pane, Priority.ALWAYS);
    }

    /**
     * called when host disconnects from its host
     */
    private void onLeave(){
        Platform.runLater(() -> hBox.getChildren().set(2, joinButton));
        host.setConnectionInterruptedListener(null);
    }

    /**
     * event handler for the join button
     *
     * @param e event that was generated
     */
    private void onJoin(ActionEvent e){
        new Thread(() -> {
            try {
                host.connect();
                Platform.runLater(() -> hBox.getChildren().set(2, leaveButton));
                host.setConnectionInterruptedListener(() -> {
                    //on kick
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                String.format("You have been kicked from %s", host.getHostName()));
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
                removeConnection.accept(host);
            }
        }).start();
    }

    /**
     * updates the item, called by the listview
     *
     * @param host the host that this cell will represent
     * @param empty whether this cell is empty
     */
    @Override protected void updateItem(HostConnection host, boolean empty){
        super.updateItem(host, empty);

        if(empty || host == null){
            this.host = null;
            Platform.runLater(() -> super.setGraphic(null));
        }
        else{
            this.host = host;

            ipLabel.setText(this.host.getHostName());
            if (host.isConnected()) {
                hBox.getChildren().set(2, leaveButton);
                this.host.setConnectionInterruptedListener(this::onLeave);
            } else {
                hBox.getChildren().set(2, joinButton);
                this.host.setConnectionInterruptedListener(null);
            }

            Platform.runLater(() -> super.setGraphic(hBox));
        }
    }
}

