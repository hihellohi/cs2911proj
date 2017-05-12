package View;

import Model.*;
import Model.RemoteMapModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by willi on 9/05/2017.
 */
public class UIController {
    private static final int PORT = 1337;
    private static final int NPLAYERS = 1;
    private static final String HOST = "localhost";

    public void switchToGame(ActionEvent actionEvent) {
        LocalMapModel model = new LocalMapModel("input1.txt");
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        startGame(stage, model);
    }

    public void startHost(ActionEvent actionEvent){
        LocalMapModel model = new LocalMapModel("input1.txt");
        ClientConnection[] hosts = new ClientConnection[NPLAYERS];
        ServerSocket welcomingSocket = null;
        try {
            welcomingSocket = new ServerSocket(PORT);
            for(int i = 0; i < NPLAYERS; i++){
                hosts[i] = new ClientConnection(model, welcomingSocket.accept(), i);
            }
        }
        catch(IOException e){
            //THIS NEEDS TO BE GRACEFULLY HANDLED. IT CAN AND WILL HAPPEN
            System.out.println("Port already used! :(");

            for(int i = 0; i < NPLAYERS; i++){
                if(hosts[i] != null){
                    hosts[i].close();
                }
            }
        }
        finally {
            if(welcomingSocket != null && !welcomingSocket.isClosed()) {
                try {
                    welcomingSocket.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        startGame(stage, model);
        for(int i = 0; i < NPLAYERS; i++){
            hosts[i].start();
        }
    }

    public void startClient(ActionEvent actionEvent){
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        RemoteMapModel client = new RemoteMapModel(HOST, PORT);
        client.start();
        startGame(stage, client);
    }

    private void startGame(Stage stage, MapModel model){
        MapView grid = new MapView(model);

        Scene gameScene = new Scene(grid, grid.mapWidth(), grid.mapHeight());
        grid.requestFocus();

        stage.setScene(gameScene);
        stage.show();
    }

    public void closeGame(ActionEvent actionEvent) {
        Platform.exit();
    }
}
