package Model.Netcode;

import Model.*;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

/**
 * Represents a connection to a host
 *
 * @author Kevin Ni
 */
public class ClientConnection {

    private LocalMapModel model;
    private DataInputStream in;
    private DataOutputStream out;
    private int player;
    private Socket socket;
    private Consumer<ClientConnection> onClientRemovedFromLobby;
    private String name;

    private Consumer<MapUpdateInfo> onMapChange = (updateInfo) -> {
        if(updateInfo == null){
            close();
            return;
        }

        try {
            out.writeBoolean(updateInfo.isNewMap());
            out.writeBoolean(updateInfo.isFinished());
            out.writeInt(updateInfo.size());
            for (Pair<Position, MapTile> change : updateInfo.getCoordinates()) {
                Position pos = change.first();
                MapTile tile = change.second();
                out.writeInt(pos.getX());
                out.writeInt(pos.getY());
                out.writeBoolean(tile.getIsGoal());
                out.writeInt(tile.getItem().ordinal());
                out.writeInt(tile.getPlayer());
            }
        }
        catch(IOException e){
            closeAndRemoveFromModel();
            e.printStackTrace();
        }
    };

    /**
     * Class constructor
     *
     * @param socket the socket to be used for this connection
     * @param onClientRemovedFromLobby callback invoked when the connection is closed in the lobby
     */
    ClientConnection(Socket socket, Consumer<ClientConnection> onClientRemovedFromLobby){
        super();

        this.onClientRemovedFromLobby = onClientRemovedFromLobby;
        this.socket = socket;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            name = in.readUTF();
            if(name.isEmpty()){
                name = socket.getInetAddress().getHostName();
            }

            System.out.println(String.format("%s connected!", socket.getInetAddress().getHostAddress()));

            new Thread(this::listen).start();
        }
        catch(IOException e){
            e.printStackTrace();
            closeAndRemoveFromModel();
        }
    }

    /**
     * Attaches a model to the connection and signals the client that the game has started
     *
     * @param model model to be attached to the connection
     * @param player player number of the client
     * @throws IOException if the connection has already been closed
     */
    void StartGame(LocalMapModel model, int player) throws IOException{
        out.writeInt(player);
        out.writeInt(model.getWidth());
        out.writeInt(model.getHeight());

        this.player = player;
        this.model = model;

        onClientRemovedFromLobby = null;

        model.subscribeModelUpdate(onMapChange);
    }

    /**
     * listen for input from the client until the socket is closed
     */
    private void listen () {
        while(!socket.isClosed()){
            try {
                KeyCode move = Constants.CODES[in.readInt()];
                model.processInput(move, player);
            }
            catch (SocketException | EOFException ex){
                closeAndRemoveFromModel();
                System.out.println("Connection closed");
            }
            catch (IOException ex){
                closeAndRemoveFromModel();
                ex.printStackTrace();
            }
        }
    }

    /**
     * closes the connection
     */
    private synchronized void close(){
        try {
            if(!socket.isClosed()) {
                socket.close();
            }

            if(onClientRemovedFromLobby != null){
                onClientRemovedFromLobby.accept(this);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * closes the connection and detaches the class from the associated model (if it exists)
     */
    public synchronized void closeAndRemoveFromModel(){
        if(model != null) {
            model.unSubscribeModelUpdate(onMapChange);
            model.killPlayer(player);
            model = null;
        }
        close();
    }

    /**
     * get the name of the client
     *
     * @return the name of the client
     */
    public String getClientName(){
        return name;
    }
}
