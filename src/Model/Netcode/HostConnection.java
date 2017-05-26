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
     * @param startGame callback that is invoked when the host signals that the game has started. Called with this
     *                  object.
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
     * @throws IOException if unable to connect to address
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

    /**
     * closes the socket and thread associated with this connetion
     */
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

    /**
     * if the connection was closed on the other side run the listener
     */
    private void onConnectionInterrupt(){
        if(socket != null){
            if(connectionInterruptedListener != null){
                connectionInterruptedListener.run();
            }
            close();
        }
    }

    /**
     * listens for updates from the host until the socket is closed
     */
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

    /**
     * broadcasts an update received from the host to all listeners registered with subscribeModelUpdate
     *
     * @throws IOException if socket is closed
     */
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

    /**
     * gets the client's player number
     *
     * @return the client's player number
     */
    public int getPlayer(){
        return player;
    }

    /**
     * handles a key pressed event and sends it to the host if it is an arrow key
     *
     * @param e the generated event
     */
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

    /**
     * gets the height (y dimension) of the map in tiles
     *
     * @return the height of the map in tiles
     */
    public int getHeight(){
        return height;
    }

    /**
     * gets the width (x dimension) of the map in tiles
     *
     * @return the width of the map in tiles
     */
    public int getWidth(){
        return width;
    }

    /**
     * gets the name of the host. If it is empty, get the server name that the host is running on. If that can't be
     * found, return the ip address of the server that the host is running on
     *
     * @return the name of the host.
     */
    public String getHostName(){
        return name;
    }

    /**
     * gets the unique id string of the host
     *
     * @return the id string of the host
     */
    String getIdString(){
        return id;
    }

    /**
     * checks if this class has an established connection with the host
     *
     * @return true if the class has an established connection with the host
     */
    public boolean isConnected(){
        return socket != null && socket.isConnected();
    }

    /**
     * Subscribes a listener that is called when an update from the host is received
     *
     * @param listener the callback that is to be invoked on receiving an update from the host. Invoked with
     *                 a MapUpdateInfo object that was broadcasted by the host's LocalMapModel.
     */
    public void subscribeModelUpdate(Consumer<MapUpdateInfo> listener){
        gameChangeListeners.add(listener);
    }

    /**
     * sets the callback that is invoked when the connection to the host is interrupted (if the host closes or
     * resets the connection NOT when close is called on this object).
     *
     * @param listener the callback to be invoked on connection interrupt.
     */
    public void setConnectionInterruptedListener(Runnable listener){
        connectionInterruptedListener = listener;
    }
}
