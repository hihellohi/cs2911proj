package Model.Netcode;

import Model.LocalMapModel;
import Model.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * @author Kevin Ni
 */
public class LobbyModel {

    private ServerSocket welcomingSocket;
    private ObservableList<ClientConnection> connectionSockets;
    private HostBeacon beacon;

    public LobbyModel() throws IOException{
        super();
        connectionSockets = FXCollections.observableList(new ArrayList<>());
        welcomingSocket = new ServerSocket(Settings.getInstance().getTCPPort());
        beacon = new HostBeacon();
        new Thread(this::listen).start();
    }

    private void listen() {
        while(!welcomingSocket.isClosed()){
            try{
                ClientConnection newConnection = new ClientConnection(welcomingSocket.accept(), (con) -> {
                    connectionSockets.remove(con);
                });

                connectionSockets.add(newConnection);
            }
            catch (SocketException | EOFException ex){
                System.out.println("Welcoming socket closed, lobby thread ending...");
            }
            catch (IOException ex) {
                abort();
                ex.printStackTrace();
            }
        }
    }

    public synchronized void close(){
        beacon.close();
        if(!welcomingSocket.isClosed()) {
            try {
                welcomingSocket.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void startGame(LocalMapModel model){
        int i = 1;
        for(ClientConnection connection : connectionSockets) {
            try {
                connection.StartGame(model, i++);
            }
            catch (IOException ex){
                ex.printStackTrace();
                connection.closeAndRemoveFromModel();
            }
        }
    }

    public void abort(){
        new ArrayList<>(connectionSockets).forEach(ClientConnection::closeAndRemoveFromModel);
        close();
    }

    public ObservableList<ClientConnection> getObservable(){
        return connectionSockets;
    }

    public int nPlayers(){
        return connectionSockets.size();
    }
}
