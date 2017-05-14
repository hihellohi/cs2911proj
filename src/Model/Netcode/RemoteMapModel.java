package Model.Netcode;

import Model.*;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * @author Kevin Ni
 */
public class RemoteMapModel extends Thread implements MapModel {
    private static final ProtocolHeader[] HEADERS = ProtocolHeader.values();
    private static final MapTile.MapItem[] TILES = MapTile.MapItem.values();

    private Socket socket;
    private List<ModelEventHandler<MapUpdateInfo>> listeners;
    private DataInputStream in;
    private DataOutputStream out;

    private int width;
    private int height;
    private int score;
    private long time;

    private Semaphore semaphore;
    private MapTile lastQuery;

    public RemoteMapModel(String host, int port){
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port));
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            width = in.readInt();
            height = in.readInt();
            score = 0;
            time = 0;

            System.out.println("Connected!");
        }
        catch(IOException e){
            System.out.println(e);
            close();
        }
        listeners = new ArrayList<>();
        semaphore = new Semaphore(0);
    }

    public void close(){
        if(listeners != null) {
            MapUpdateInfo info = new MapUpdateInfo(true);
            for (ModelEventHandler<MapUpdateInfo> listener : listeners) {
                listener.handle(info);
            }
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
                    case ANSWER:
                        lastQuery = new MapTile(in.readBoolean(), TILES[in.readInt()]);
                        semaphore.release();
                        break;
                    default:
                        System.out.println("unknown command!");
                        break;
                }
            }
            catch (SocketException | EOFException ex){
                close();
                System.out.println("Connection closed");
            }
            catch (IOException ex){
                close();
                ex.printStackTrace();
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
        score++;

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
        try {
            //THIS IS NOT OPTIMAL CHANGE THIS
            out.writeByte(ProtocolHeader.QUERY.ordinal());
            out.writeInt(pos.getX());
            out.writeInt(pos.getY());
            semaphore.acquire();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return lastQuery;
    }

    public int getScore() {
        return score;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public void subscribeModelUpdate(ModelEventHandler<MapUpdateInfo> listener){
        listeners.add(listener);
    }
}
