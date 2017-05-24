package Model.Netcode;

import Model.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Discovers hosts by sending pings to them and listening for their replies
 *
 * @author Kevin Ni
 */
public class BeaconFinder {
    private DatagramSocket socket;
    private ObservableList<HostConnection> observableList;
    private Set<String> seenAddresses;
    private Consumer<HostConnection> onGameStart;

    /**
     * class constructor
     *
     * @param onGameStart callback that is invoked when a connected host starts the game
     * @throws SocketException when desired socket is already occupied
     */
    public BeaconFinder(Consumer<HostConnection> onGameStart) throws SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        observableList = FXCollections.observableList(new ArrayList<HostConnection>());
        seenAddresses = new HashSet<>();
        this.onGameStart = onGameStart;
        new Thread(this::listener).start();
    }

    /**
     * pings a host
     *
     * @param hostName the host to be pinged
     */
    public void target(String hostName) throws UnknownHostException {
        try {
            DatagramPacket packet = new DatagramPacket(
                    Constants.BEACON_MESSAGE.getBytes(),
                    Constants.BEACON_MESSAGE.length(),
                    InetAddress.getByName(hostName),
                    Settings.getInstance().getTCPPort());
            socket.send(packet);
        }
        catch (UnknownHostException ex){
            throw ex;
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * pings all hosts on the subnet (pings the broadcast address of all interfaces on the machine)
     */
    public void broadcast(){
        Settings settings = Settings.getInstance();
        try {
            Enumeration<NetworkInterface> it = NetworkInterface.getNetworkInterfaces();
            while(it.hasMoreElements()){
                for(InterfaceAddress address : it.nextElement().getInterfaceAddresses()){

                    //ignore loopback interface and IPv6
                    if (address.getAddress().getAddress()[0] != 127 && address.getBroadcast() != null){
                        System.out.println(String.format("broadcasting on %s", address.getBroadcast()));

                        DatagramPacket packet = new DatagramPacket(
                                Constants.BEACON_MESSAGE.getBytes(),
                                Constants.BEACON_MESSAGE.length(),
                                address.getBroadcast(),
                                settings.getTCPPort());
                        socket.send(packet);
                    }
                }
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * method that listens for replies to the ping and updates the observableList
     */
    private void listener() {
        DatagramPacket recv = new DatagramPacket(new byte[256], 256);
        while(!socket.isClosed()){
            try {
                socket.receive(recv);
                String response = new String(recv.getData()).trim();
                InetAddress address = recv.getAddress();
                if(!seenAddresses.contains(response)){
                    System.out.println(String.format("host %s found!", response));
                    String[] components = response.split("\\|", 2);
                    observableList.add(new HostConnection(
                            address,
                            components.length == 2 ? components[1] : address.getHostName(),
                            onGameStart));
                    seenAddresses.add(response);
                }
            }
            catch(IOException ex){
                close();
                System.out.println("Beacon finder closed. thread terminating...");
            }
        }
    }

    /**
     * get the observable list of discovered hosts
     *
     * @return observable list of discovered hosts
     */
    public ObservableList<HostConnection> getObservable(){
        return observableList;
    }

    /**
     * closes the socket and the thread used by this class. In addition,
     * closes all connections except one in preparation for start of game
     *
     * @param started The connection that has been started and should not be closed
     */
    public synchronized void finish(HostConnection started){
        for(HostConnection model : observableList){
            if(model != started) {
                model.close();
            }
        }
        close();
    }

    /**
     * closes the socket and the thread used by this class
     */
    public synchronized void close(){
        if(!socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * closes the socket and the thread used by this class and closes all established connections to hosts
     */
    public void abort(){
        new ArrayList<>(observableList).forEach(HostConnection::close);
        close();
    }

    /**
     * checks whether closeAndRemoveFromModel has been called on this class before
     *
     * @return true if closeAndRemoveFromModel has been called on this class.
     */
    public boolean isLive(){
        return !socket.isClosed();
    }
}
