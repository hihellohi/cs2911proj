package View;

import Model.*;

import java.io.*;
import java.net.*;

/**
 * @author Kevin Ni
 */
public class NetworkHost extends Thread implements ModelEventHandler<MapUpdateInfo>{
    private MapModel model;
    private InputStream in;
    private DataOutputStream out;
    private int player;
    private Socket socket;

    public NetworkHost(MapModel model, Socket socket, int player){
        try {
            in = socket.getInputStream();
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
    }

    public void close(){
        try {
            if(socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    public void handle(MapUpdateInfo updateInfo){
    }
}
