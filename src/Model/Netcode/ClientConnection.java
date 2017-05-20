package Model.Netcode;

import Model.*;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

/**
 * @author Kevin Ni
 */
public class ClientConnection {

    private LocalMapModel model;
    private DataInputStream in;
    private DataOutputStream out;
    private int player;
    private Socket socket;
    private Consumer<ClientConnection> lobbyRemover;

    public ClientConnection(Socket socket, Consumer<ClientConnection> lobbyRemover){
        super();

        this.lobbyRemover = lobbyRemover;
        this.socket = socket;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            System.out.println(String.format("%s connected!", socket.getInetAddress().getHostAddress()));
        }
        catch(IOException e){
            e.printStackTrace();
            close();
        }

        new Thread(listen).start();
    }

    public void StartGame(LocalMapModel model, int player) throws IOException{
        out.writeInt(model.getWidth());
        out.writeInt(model.getHeight());

        this.player = player;
        this.model = model;

        lobbyRemover = null;

        model.subscribeModelUpdate(onMapChange);
    }

    private Runnable listen = () -> {
        while(!socket.isClosed()){
            try {
                switch(Constants.HEADERS[in.readByte()]){
                    case MOVE_REQUEST:
                        model.processInput(Constants.CODES[in.readInt()], player);
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

    public void close(){

        try {
            if(!socket.isClosed()) {
                socket.close();
            }

            //TODO probably implement kick all here?
            if(lobbyRemover != null){
                lobbyRemover.accept(this);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    private Consumer<MapUpdateInfo> onMapChange = (updateInfo) -> {
        try {
            out.writeByte(ProtocolHeader.MOVE.ordinal());
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
            }
        }
        catch(IOException e){
            close();
            e.printStackTrace();
        }
    };

    public String getHostAddress(){
        return socket.getInetAddress().getHostAddress();
    }
}
