package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocationBroadcast extends Thread{
    private final int tcpLocation;
    private final String serverName;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public LocationBroadcast(int tcpLocation, String serverName) {
        super("LocationBroadcast");
        this.tcpLocation = tcpLocation;
        this.serverName = serverName;
    }

    @Override
    public void run(){
        running.set(true);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] buffer = (serverName + "__" + tcpLocation).getBytes();
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
//            InetAddress multicastAddress = InetAddress.getByName("239.255.255.255");

            while (running.get()) {
                DatagramPacket packet = null;
                packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 6666);
                socket.send(packet);
                try {
                    sleep(1000L);
                } catch (InterruptedException e) { }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopBroadcasting(){running.set(false);}
}
