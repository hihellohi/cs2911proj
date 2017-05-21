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
 * @author Kevin Ni
 */
public class BeaconFinder {
    private DatagramSocket socket;
    private ObservableList<RemoteMapModel> observableList;
    private Set<String> seenAddresses;
    private Consumer<RemoteMapModel> onGameStart;

    public BeaconFinder(Consumer<RemoteMapModel> onGameStart) throws SocketException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        observableList = FXCollections.observableList(new ArrayList<RemoteMapModel>());
        seenAddresses = new HashSet<>();
        this.onGameStart = onGameStart;
        new Thread(this::listener).start();
    }

    /**
     * find a beacon at this address
     */
    public void target(String hostName){
        try {
            DatagramPacket packet = new DatagramPacket(
                    Constants.BEACON_MESSAGE.getBytes(),
                    Constants.BEACON_MESSAGE.length(),
                    InetAddress.getByName(hostName),
                    Settings.getInstance().getUDPPort());
            socket.send(packet);
        }
        catch (UnknownHostException ex){
            System.out.println("unknown host");
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * find all beacons on the subnet
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
                                settings.getUDPPort());
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

    public void close(){
        if(!socket.isClosed()) {
            socket.close();
        }
    }

    public void abort(){
        new ArrayList<>(observableList).forEach(RemoteMapModel::close);
        close();
    }
}
