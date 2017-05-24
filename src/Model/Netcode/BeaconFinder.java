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
 * Sends pings to desired addresses and updates an ObservableList when it receives replies.
 *
 * @author Kevin Ni
 */
public class BeaconFinder {
    private DatagramSocket socket;
    private ObservableList<RemoteMapModel> observableList;
    private Set<String> seenAddresses;
    private Consumer<RemoteMapModel> onGameStart;

    /**
     *
     * @param onGameStart callback that is invoked when a connected host starts the game
     * @throws SocketException when desired socket is already occupied
     */
    public BeaconFinder(Consumer<RemoteMapModel> onGameStart) throws SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        observableList = FXCollections.observableList(new ArrayList<RemoteMapModel>());
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

    private void listener() {
        DatagramPacket recv = new DatagramPacket(new byte[256], 256);
        while(!socket.isClosed()){
            try {
                socket.receive(recv);
                String name = new String(recv.getData()).trim();
                InetAddress address = recv.getAddress();
                if(!seenAddresses.contains(name)){
                    System.out.println(String.format("host %s found!", name));
                    observableList.add(new RemoteMapModel(address, onGameStart));
                    seenAddresses.add(name);
                }
            }
            catch(IOException ex){
                close();
                System.out.println("Beacon finder closed. thread terminating...");
            }
        }
    }

    public ObservableList<RemoteMapModel> getObservable(){
        return observableList;
    }

    public synchronized void finish(RemoteMapModel spare){
        for(RemoteMapModel model : observableList){
            if(model != spare) {
                model.close();
            }
        }
        close();
    }

    public synchronized void close(){
        if(!socket.isClosed()) {
            socket.close();
        }
    }

    public void abort(){
        new ArrayList<>(observableList).forEach(RemoteMapModel::close);
        close();
    }

    public boolean isLive(){
        return socket.isClosed();
    }
}
