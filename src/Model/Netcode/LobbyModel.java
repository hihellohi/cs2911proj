package Model.Netcode;

import Model.LocalMapModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * @author Kevin Ni
 */
public class LobbyModel extends Thread{

    private ServerSocket welcomingSocket;
    private ObservableList<ClientConnection> connectionSockets;
    private HostBeacon beacon;

    public LobbyModel() throws IOException{
        super();
        connectionSockets = FXCollections.observableList(new ArrayList<>());
        welcomingSocket = new ServerSocket(Constants.TCP_PORT);
        beacon = new HostBeacon();
        super.start();
    }

    @Override public void run(){
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

    public void close(){
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

    public void finish(LocalMapModel model){
        int i = 1;
        for(ClientConnection connection : connectionSockets) {
            try {
                connection.StartGame(model, i++);
            }
            catch (IOException ex){
                ex.printStackTrace();
                connection.close();
            }
        }
    }

    public void abort(){
        new ArrayList<>(connectionSockets).forEach((conn)->{
            conn.close();
        });
        close();
    }

    public ObservableList<ClientConnection> getObservable(){
        return connectionSockets;
    }

    public int nPlayers(){
        return connectionSockets.size();
    }
}
