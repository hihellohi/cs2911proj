package Model.Netcode;

import Model.*;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Kevin Ni
 */
public class RemoteMapModel implements MapModel {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private InetSocketAddress host;

    private List<Consumer<MapUpdateInfo>> gameChangeListeners;
    private Consumer<RemoteMapModel> startGameListener;

    private int width;
    private int height;

    public RemoteMapModel(InetAddress host, Consumer<RemoteMapModel> startGame) {
        super();

        this.startGameListener = startGame;
        this.host = new InetSocketAddress(host, Constants.TCP_PORT);

        gameChangeListeners = new ArrayList<>();
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

        startGameListener.accept(this);
        startGameListener = null;

        while(socket != null && !socket.isClosed()){
            try {
                switch(Constants.HEADERS[in.readByte()]){
                    case MOVE:
                        broadcast();
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
        MapUpdateInfo info = new MapUpdateInfo(in.readBoolean(), in.readBoolean());
        int n = in.readInt();
        for(int i = 0; i < n; i++){
            Position position = new Position(in.readInt(), in.readInt());
            MapTile mapTile = new MapTile(in.readBoolean(), Constants.TILES[in.readInt()]);
            info.addChange(position, mapTile);
        }

        for(Consumer<MapUpdateInfo> listener : gameChangeListeners) {
            listener.accept(info);
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

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public String getHostName(){
        return host.getAddress().getHostAddress();
    }

    public boolean isConnected(){
        return socket != null && socket.isConnected();
    }

    public void subscribeModelUpdate(Consumer<MapUpdateInfo> listener){
        gameChangeListeners.add(listener);
    }

    public void generateNewMap() {

    }

    public void undo() {

    }

    public void reset() {

    }
}
