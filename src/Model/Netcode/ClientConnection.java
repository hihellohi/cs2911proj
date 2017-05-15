package Model.Netcode;

import Model.*;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

/**
 * @author Kevin Ni
 */
public class ClientConnection extends Thread implements ModelEventHandler<MapUpdateInfo> {

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

        super.start();
    }

    public void StartGame(LocalMapModel model, int player) throws IOException{
        out.writeInt(model.getWidth());
        out.writeInt(model.getHeight());

        this.player = player;
        this.model = model;

        lobbyRemover = null;

        model.subscribeModelUpdate(this);
    }

    @Override public void run(){
        while(!socket.isClosed()){
            try {
                switch(Constants.HEADERS[in.readByte()]){
                    case MOVE_REQUEST:
                        model.processInput(Constants.CODES[in.readInt()]);
                        break;
                    case QUERY:
                        query(new Position(in.readInt(), in.readInt()));
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

    private void query(Position pos) throws IOException{
        MapTile tile = model.getMapAt(pos);
        out.writeByte(ProtocolHeader.ANSWER.ordinal());
        out.writeBoolean(tile.getIsGoal());
        out.writeInt(tile.getItem().ordinal());
    }

    public void close(){

        try {
            if(!socket.isClosed()) {
                socket.close();
            }

            if(lobbyRemover != null){
                lobbyRemover.accept(this);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public void handle(MapUpdateInfo updateInfo){
        try {
            out.writeByte(ProtocolHeader.MOVE.ordinal());
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
    }

    public String getHostAddress(){
        return socket.getInetAddress().getHostAddress();
    }
}
