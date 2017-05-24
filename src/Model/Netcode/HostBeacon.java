package Model.Netcode;

import Model.Settings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

/**
 * @author Kevin Ni
 */
public class HostBeacon {
    private DatagramSocket socket;
    private String beaconName;

    public HostBeacon() throws SocketException {
        super();
        socket = new DatagramSocket(Settings.getInstance().getTCPPort());
        beaconName = UUID.randomUUID().toString();
        new Thread(this::listen).start();
    }

    private void listen () {
        DatagramPacket recv = new DatagramPacket(new byte[256], 256);
        while(!socket.isClosed()){
            try {
                socket.receive(recv);
                String string = new String(recv.getData()).trim();
                if(string.equals(Constants.BEACON_MESSAGE)){
                    DatagramPacket send = new DatagramPacket(
                            beaconName.getBytes(),
                            beaconName.length(),
                            recv.getAddress(),
                            recv.getPort());
                    socket.send(send);
                }
            }
            catch(IOException ex){
                close();
                System.out.println("Beacon socket closed. beacon thread terminating...");
            }
        }
    }

    public synchronized void close(){
        if(!socket.isClosed()) {
            socket.close();
        }
    }
}
