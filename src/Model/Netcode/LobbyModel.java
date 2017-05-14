package Model.Netcode;

import Model.LocalMapModel;
import Model.ModelEventHandler;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Ni
 */
public class LobbyModel extends Thread{
    private static final int PORT = 1337;

    private ServerSocket welcomingSocket;
    private List<Socket> connectionSockets;
    private List<ModelEventHandler<Socket>> listeners;

    public LobbyModel() throws IOException{
        connectionSockets = new ArrayList<>();
        welcomingSocket = new ServerSocket(PORT);
        listeners = new ArrayList<>();
    }

    @Override public void run(){
        while(!welcomingSocket.isClosed()){
            try{
                Socket newConnection = welcomingSocket.accept();
                connectionSockets.add(newConnection);

                for(ModelEventHandler<Socket> listener : listeners){
                    listener.handle(newConnection);
                }
            }
            catch (SocketException | EOFException ex){
                System.out.println("Socket closed, lobby thread ending...");
            }
            catch (IOException ex) {
                closeConnections();
                close();
                ex.printStackTrace();
            }
        }
    }

    private void closeConnections(){
        for (Socket socket : connectionSockets)
        {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private void close(){
        if(welcomingSocket != null && !welcomingSocket.isClosed()) {
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
        for(Socket socket : connectionSockets) {
            new ClientConnection(model, socket, i++).start();
        }
        close();
    }

    public void subscribe(ModelEventHandler<Socket> listener){
        listeners.add(listener);
    }
}
