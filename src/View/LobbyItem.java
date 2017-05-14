package View;

import Model.Netcode.LobbyModel;
import com.sun.javafx.font.freetype.HBGlyphLayout;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.net.Socket;

/**
 * @author Kevin Ni
 */
public class LobbyItem extends ListCell<Socket> {
    private HBox hBox;
    private Label ipLabel;
    private Socket currentSocket;

    public LobbyItem(LobbyModel model){
        super();

        super.setPrefHeight(30);

        hBox = new HBox();
        ipLabel = new Label();
        currentSocket = null;

        Pane pane = new Pane();
        Button button = new Button("Kick");

        super.setText(null);

        hBox.getChildren().addAll(ipLabel, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setOnAction((e)->{
            model.kick(currentSocket);
        });
    }

    @Override protected void updateItem(Socket socket, boolean empty){
        super.updateItem(socket, empty);

        if(empty || socket == null){
            currentSocket = null;
            Platform.runLater(() ->{
                super.setGraphic(null);
            });
        }
        else{
            currentSocket = socket;
            ipLabel.setText(socket.getInetAddress().getHostAddress());
            Platform.runLater(() ->{
                super.setGraphic(hBox);
            });
        }
    }
}
