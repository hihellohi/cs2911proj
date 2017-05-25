package Model.Netcode;

import Model.*;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * A connection to the host. Represents the LocalMapModel present on the host machine
 *
 * @author Kevin Ni
 */
public class HostConnection implements MapModel {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private InetSocketAddress host;
    private String name;
    private String id;

    private List<Consumer<MapUpdateInfo>> gameChangeListeners;
    private Consumer<HostConnection> startGameListener;
    private Runnable connectionInterruptedListener;

    private int width;
    private int height;
    private int player;

    /**
     * Class constructor
     *
     * @param host the address of the host
     * @param id the id of the host
     * @param startGame callback that is invoked when the host signals that the game has started
     */
    HostConnection(InetAddress host, String id, Consumer<HostConnection> startGame) {
        super();

        this.id = id;
        String[] components = id.split("\\|", 2);
        if(components.length == 2){
            name = components[1];
        }
        else{
            name = host.getHostName();
        }

        this.startGameListener = startGame;
        this.host = new InetSocketAddress(host, Settings.getInstance().getTCPPort());

        gameChangeListeners = new ArrayList<>();
    }

    /**
     * attempts to connect to the host address
     *
     * @throws IOException
     */
    public void connect() throws IOException {
        try {
            socket = new Socket();
            socket.connect(host);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            String name = Settings.getInstance().getName();
            out.writeUTF(name);

            System.out.println("Connected!");
        }
        catch(IOException e){
            close();
            throw e;
        }
        new Thread(this::listen).start();
    }

    public synchronized void close(){
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

    private void onConnectionInterrupt(){
        //if the connection was closed on the other side run the listener
        if(socket != null){
            if(connectionInterruptedListener != null){
                connectionInterruptedListener.run();
            }
            close();
        }
    }

    private void listen(){
        if(socket == null){
            return;
        }

        try {
            player = in.readInt();
            width = in.readInt();
            height = in.readInt();
        }
        catch (IOException ex){
            onConnectionInterrupt();
            return;
        }

        startGameListener.accept(this);
        startGameListener = null;

        while(socket != null && !socket.isClosed()){
            try {
                broadcast();
            }
            catch (SocketException | EOFException ex){
                onConnectionInterrupt();
                System.out.println("Connection closed");
            }
            catch (IOException ex){
                close();
                ex.printStackTrace();
            }
        }
    }

    private void broadcast() throws IOException{
        MapUpdateInfo info = new MapUpdateInfo(in.readBoolean(), in.readBoolean());
        int n = in.readInt();
        for(int i = 0; i < n; i++){
            Position position = new Position(in.readInt(), in.readInt());
            MapTile mapTile = new MapTile(in.readBoolean(), Constants.TILES[in.readInt()], in.readInt());
            info.addChange(position, mapTile);
        }

        for(Consumer<MapUpdateInfo> listener : gameChangeListeners) {
            listener.accept(info);
        }
    }

    public int getPlayer(){
        return player;
    }

    public void handle(KeyEvent e){
        switch (e.getCode()){
            case UP:case DOWN:case LEFT: case RIGHT:
                try {
                    out.writeInt(e.getCode().ordinal());
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
        }
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public String getHostName(){
        return name;
    }

    String getIdString(){
        return id;
    }

    public boolean isConnected(){
        return socket != null && socket.isConnected();
    }

    public void subscribeModelUpdate(Consumer<MapUpdateInfo> listener){
        gameChangeListeners.add(listener);
    }

    public void setConnectionInterruptedListener(Runnable listener){
        connectionInterruptedListener = listener;
    }
}
