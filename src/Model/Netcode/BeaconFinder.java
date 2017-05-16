package Model.Netcode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kevin Ni
 */
public class BeaconFinder extends Thread {
    private DatagramSocket socket;
    private ObservableList<String> responses;
    private Set<String> seenAddresses;

    public BeaconFinder() throws SocketException {
        super();
        socket = new DatagramSocket();
        socket.setBroadcast(true);
        responses = FXCollections.observableList(new ArrayList<String>());
        seenAddresses = new HashSet<>();
        super.start();
    }

    public void target(String hostName){
        try {
            DatagramPacket packet = new DatagramPacket(
                    Constants.BEACON_MESSAGE.getBytes(),
                    Constants.BEACON_MESSAGE.length(),
                    InetAddress.getByName(hostName),
                    Constants.UDP_PORT);
            socket.send(packet);
        }
        catch (UnknownHostException ex){
            System.out.println("unknown host");
            ex.printStackTrace();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void broadcast(){
        try {
            InetAddress localhost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localhost);

            for(InterfaceAddress address : networkInterface.getInterfaceAddresses()){
                if(address.getBroadcast() != null) {
                    System.out.println(address.getBroadcast().toString());
                    DatagramPacket packet = new DatagramPacket(
                            Constants.BEACON_MESSAGE.getBytes(),
                            Constants.BEACON_MESSAGE.length(),
                            address.getBroadcast(),
                            Constants.UDP_PORT);
                    socket.send(packet);
                }
            }
        }
        catch (UnknownHostException ex){
            System.out.println("unknown host");
            ex.printStackTrace();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override public void run(){
        DatagramPacket recv = new DatagramPacket(new byte[256], 256);
        while(!socket.isClosed()){
            try {
                socket.receive(recv);
                String string = new String(recv.getData()).trim();
                if(string.equals(Constants.BEACON_MESSAGE) && !seenAddresses.contains(string)){
                    responses.add(recv.getAddress().getHostName());
                    seenAddresses.add(string);
                }
            }
            catch(IOException ex){
                close();
            }
        }
    }

    public ObservableList<String> getObservable(){
        return responses;
    }

    public void close(){
        if(!socket.isClosed()) {
            socket.close();
        }
    }
}
