package View;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Ni on 11/05/2017.
 */
public class NetworkClient {
    private Socket socket;

    public NetworkClient(String host, int port){
        try {
            socket = new Socket(host, port);
            System.out.println("Connected!");
        }
        catch(IOException e){
            System.out.println(e);
            close();
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
}
