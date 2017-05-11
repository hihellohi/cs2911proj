package View;

import Model.*;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by Ni on 11/05/2017.
 */
class NetworkClient implements IMapModel {
    private Socket socket;
    private List<ModelEventHandler<MapUpdateInfo>> listeners;
    private DataInputStream in;
    private DataOutputStream out;

    NetworkClient(String host, int port){
        try {
            socket = new Socket(host, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected!");
        }
        catch(IOException e){
            System.out.println(e);
            close();
        }
        listeners = new ArrayList<>();
    }

    void close(){
        try {
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    public void handle(KeyEvent e){
        try {
            out.writeInt(e.getCode().ordinal());
        }
        catch (IOException ex){
            System.out.println(ex);
        }
    }

    public MapTile getMapAt(Position pos){
        return new MapTile(false, MapTile.MapItem.GROUND);
    }

    public int getHeight(){
        return 9;
    }

    public int getWidth(){
        return 8;
    }

    public void subscribeModelUpdate(ModelEventHandler<MapUpdateInfo> listener){
        listeners.add(listener);
    }
}
