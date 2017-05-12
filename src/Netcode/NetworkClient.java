package Netcode;

import Model.*;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * Created by Ni on 11/05/2017.
 */
public class NetworkClient extends Thread implements IMapModel {
    private static final ProtocolHeader[] HEADERS = ProtocolHeader.values();
    private static final MapTile.MapItem[] TILES = MapTile.MapItem.values();

    private Socket socket;
    private List<ModelEventHandler<MapUpdateInfo>> listeners;
    private DataInputStream in;
    private DataOutputStream out;

    public NetworkClient(String host, int port){
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
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

    public void close(){
        MapUpdateInfo info = new MapUpdateInfo(false);
        for(ModelEventHandler<MapUpdateInfo> listener : listeners) {
            listener.handle(info);
        }

        try {
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override public void run(){
        while(!socket.isClosed()){
            try {
                switch(HEADERS[in.readByte()]){
                    case MOVE:
                        broadcast();
                        break;
                    default:
                        System.out.println("unknown command!");
                }
            }
            catch (IOException ex){
                close();
                if(ex instanceof EOFException) {
                    System.out.println("connection closed");
                }
                else{
                    ex.printStackTrace();
                }
            }
        }
    }

    private void broadcast() throws IOException{
        int n = in.readInt();
        MapUpdateInfo info = new MapUpdateInfo(false);
        for(int i = 0; i < n; i++){
            Position position = new Position(in.readInt(), in.readInt());
            MapTile mapTile = new MapTile(in.readBoolean(), TILES[in.readInt()]);
            info.addChange(position, mapTile);
        }

        for(ModelEventHandler<MapUpdateInfo> listener : listeners) {
            listener.handle(info);
        }
    }

    public void handle(KeyEvent e){
        try {
            out.writeByte(ProtocolHeader.MOVE_REQUEST.ordinal());
            out.writeInt(e.getCode().ordinal());
        }
        catch (IOException ex){
            ex.printStackTrace();
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
