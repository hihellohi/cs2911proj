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
 * Represents a lobby. Keeps track of established connections and welcomes new ones
 *
 * @author Kevin Ni
 */
public class LobbyModel {

    private ServerSocket welcomingSocket;
    private ObservableList<ClientConnection> connectionSockets;
    private HostBeacon beacon;

    /**
     * class constructor
     *
     * @throws IOException when port is already occupied
     */
    public LobbyModel() throws IOException{
        super();
        connectionSockets = FXCollections.observableList(new ArrayList<>());
        welcomingSocket = new ServerSocket(Settings.getInstance().getTCPPort());
        beacon = new HostBeacon();
        new Thread(this::listen).start();
    }

    /**
     * listens for new incoming connections and creates new connection objects for them until the close is called on
     * this object.
     */
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

    /**
     * closes the welcoming socket and stops any threads from listening for new connections
     */
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

    /**
     * attaches a model to all established connections and signals to all clients that the game has started
     *
     * @param model the model to be attached
     *
     * @ore model != null
     */
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

    /**
     * closes the welcoming socket and closes all established connections
     */
    public void abort(){
        new ArrayList<>(connectionSockets).forEach(ClientConnection::close);
        close();
    }

    /**
     * get an observableList containing all established connections
     * @return observableList containing all established connections
     */
    public ObservableList<ClientConnection> getObservable(){
        return connectionSockets;
    }

    /**
     * get the current number of established connections
     * @return the current number of established connections
     */
    public int nPlayers(){
        return connectionSockets.size();
    }
}
