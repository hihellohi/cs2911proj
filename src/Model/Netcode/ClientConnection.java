package Model.Netcode;

import Model.*;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.*;

/**
 * @author Kevin Ni
 */
public class ClientConnection extends Thread implements ModelEventHandler<MapUpdateInfo> {
    private static final ProtocolHeader[] HEADERS = ProtocolHeader.values();
    private static final KeyCode[] CODES = KeyCode.values();

    private LocalMapModel model;
    private DataInputStream in;
    private DataOutputStream out;
    private int player;
    private Socket socket;

    public ClientConnection(LocalMapModel model, Socket socket, int player){
        super();
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(model.getWidth());
            out.writeInt(model.getHeight());

            System.out.println(String.format("%s connected!", socket.getInetAddress().getHostAddress()));
        }
        catch(IOException e){
            e.printStackTrace();
            close();
        }

        this.socket = socket;
        this.player = player;
        this.model = model;
        model.subscribeModelUpdate(this);

        super.start();
    }

    @Override public void run(){

        while(!socket.isClosed()){
            try {
                switch(HEADERS[in.readByte()]){
                    case MOVE_REQUEST:
                        model.processInput(CODES[in.readInt()]);
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
            if(socket != null && !socket.isClosed()) {
                socket.close();
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
}
