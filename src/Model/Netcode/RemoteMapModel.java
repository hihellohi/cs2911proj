package Model.Netcode;

import Model.*;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * @author Kevin Ni
 */
public class RemoteMapModel implements MapModel {

    private Socket socket;
    private List<ModelEventHandler<MapUpdateInfo>> listeners;
    private DataInputStream in;
    private DataOutputStream out;
    private InetSocketAddress host;

    private Consumer<RemoteMapModel> startGame;

    private int width;
    private int height;
    private int score;
    private long time;

    private Semaphore semaphore;
    private MapTile lastQuery;

    public RemoteMapModel(InetAddress host, Consumer<RemoteMapModel> startGame) {
        super();

        this.startGame = startGame;
        this.host = new InetSocketAddress(host, Constants.TCP_PORT);

        score = 0;
        time = 0;

        listeners = new ArrayList<>();
        semaphore = new Semaphore(0);
    }

    public void connect() throws IOException {
        try {
            socket = new Socket();
            socket.connect(host);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected!");
        }
        catch(IOException e){
            close();
            throw e;
        }
        new Thread(listen).start();
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
                socket = null;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private Runnable listen = () -> {
        if(socket == null){
            return;
        }

        try {
            width = in.readInt();
            height = in.readInt();
        }
        catch (IOException ex){
            close();
            return;
        }

        startGame.accept(this);
        startGame = null;

        while(socket != null && !socket.isClosed()){
            try {
                switch(Constants.HEADERS[in.readByte()]){
                    case MOVE:
                        broadcast();
                        break;
                    case ANSWER:
                        lastQuery = new MapTile(in.readBoolean(), Constants.TILES[in.readInt()]);
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
    };

    private void broadcast() throws IOException{
        int n = in.readInt();
        MapUpdateInfo info = new MapUpdateInfo(false);
        for(int i = 0; i < n; i++){
            Position position = new Position(in.readInt(), in.readInt());
            MapTile mapTile = new MapTile(in.readBoolean(), Constants.TILES[in.readInt()]);
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
            //TODO THIS IS NOT OPTIMAL CHANGE THIS
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

    public String getHostName(){
        return host.getAddress().getHostName();
    }

    public boolean isConnected(){
        return socket != null && socket.isConnected();
    }

    public void subscribeModelUpdate(ModelEventHandler<MapUpdateInfo> listener){
        listeners.add(listener);
    }

    public void generateNewMap() {

    }

    public void reset() {

    }
}
