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
public class LobbyItem extends ListCell<ClientConnection> {
    private HBox hBox;
    private Label ipLabel;
    private ClientConnection currentConnection;

    public LobbyItem(){
        super();

        super.setPrefHeight(30);

        hBox = new HBox();
        ipLabel = new Label();
        currentConnection = null;

        Pane pane = new Pane();
        Button button = new Button("Kick");

        super.setText(null);

        hBox.getChildren().addAll(ipLabel, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setOnAction((e) -> currentConnection.closeAndRemoveFromModel());
    }

    @Override protected void updateItem(ClientConnection connection, boolean empty){
        super.updateItem(connection, empty);

        if(empty || connection == null){
            currentConnection = null;
            Platform.runLater(() -> super.setGraphic(null));
        }
        else{
            currentConnection = connection;
            ipLabel.setText(connection.getClientName());
            Platform.runLater(() -> super.setGraphic(hBox));
        }
    }
}
