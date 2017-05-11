package View;

import Model.*;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.*;

/**
 * @author Kevin Ni
 */
public class NetworkHost extends Thread implements ModelEventHandler<MapUpdateInfo>{
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
                int i = in.readInt();
                model.processInput(KeyCode.values()[i]);
            }
            catch (IOException ex){
                System.out.println(ex);
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
            System.out.println(e);
        }
    }

    public void handle(MapUpdateInfo updateInfo){
    }
}
