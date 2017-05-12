package Netcode;

import Model.*;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.*;

/**
 * @author Kevin Ni
 */
public class NetworkHost extends Thread implements ModelEventHandler<MapUpdateInfo>{
    private static final ProtocolHeader[] HEADERS = ProtocolHeader.values();
    private static final KeyCode[] CODES = KeyCode.values();

    private MapModel model;
    private DataInputStream in;
    private DataOutputStream out;
    private int player;
    private Socket socket;

    public NetworkHost(MapModel model, Socket socket, int player){
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println(String.format("%s connected!", socket.getInetAddress().getHostAddress()));
        }
        catch(IOException e){
            System.out.println(e);
            close();
        }

        this.socket = socket;
        this.player = player;
        this.model = model;
        model.subscribeModelUpdate(this);
    }

    @Override public void run(){
        while(!socket.isClosed()){
            try {
                switch(HEADERS[in.readByte()]){
                    case MOVE_REQUEST:
                        model.processInput(CODES[in.readInt()]);
                        break;
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

            if (updateInfo.isFinished()) {
                close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
