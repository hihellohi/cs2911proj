package Model.Netcode;

import Model.Settings;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

/**
 * responds to pings from BeaconFinders with a UUID and a name
 *
 * @author Kevin Ni
 */
class HostBeacon {
    private DatagramSocket socket;
    private String beaconName;

    /**
     * Class constructor
     *
     * @throws SocketException when socket already occupied
     */
    HostBeacon() throws SocketException {
        super();
        socket = new DatagramSocket(Settings.getInstance().getTCPPort());

        String name = Settings.getInstance().getName();
        if(name.isEmpty()){
            beaconName = UUID.randomUUID().toString();
        }
        else {
            beaconName = String.format("%s|%s", UUID.randomUUID().toString(), name);
        }
        new Thread(this::listen).start();
    }

    /**
     * listens for pings and responds to them until socket is closed
     */
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

    /**
     * closes the thread and socket associated with this socket
     */
    public synchronized void close(){
        if(!socket.isClosed()) {
            socket.close();
        }
    }
}
