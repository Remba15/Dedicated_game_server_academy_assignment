package knock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MultiCastClient {
    private static final byte[] BUFFER = new byte[256];

    public static void main(String[] args) throws IOException {
        InetAddress address;
        String receivedData;
        InetAddress group = InetAddress.getByName("239.255.255.255");
        try (MulticastSocket socket = new MulticastSocket(6666)) {
            socket.joinGroup(group);
            DatagramPacket packet = new DatagramPacket(BUFFER, BUFFER.length);
            socket.receive(packet);

            address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(BUFFER, BUFFER.length, address, port);
            receivedData = new String(packet.getData(), 0, packet.getLength());
        }

        System.out.println(receivedData);
        String[] receivedDataParts = receivedData.split("__");
        String serverName = receivedDataParts[0];
        int portNumber = Integer.parseInt(receivedDataParts[1].trim());

        try (Socket kkSocket = new Socket(address, portNumber);
             PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));) {

            String fromServer;

            InputCollector inputCollector = new InputCollector(out::println);
            inputCollector.start();

            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
                if (fromServer.equals("Bye"))
                    break;
            }

            inputCollector.stopCollecting();

        } catch (IOException e) {
            System.err.println("Unexpected exception occurred: " + e);
            System.exit(1);
        }

        System.exit(0);
    }

    public static class InputCollector extends Thread {
        private final Consumer<String> inputConsumer;
        private final AtomicBoolean running = new AtomicBoolean(false);

        public InputCollector(final Consumer<String> inputConsumer) {
            super("InputCollector");
            this.inputConsumer = inputConsumer;
        }

        public void run() {
            running.set(true);
            try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
                String input = null;
                while (running.get() && (input = stdIn.readLine()) != null) {
                    inputConsumer.accept(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void stopCollecting() {
            running.set(false);
        }

    }

}
