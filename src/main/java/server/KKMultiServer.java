package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicLong;

public class KKMultiServer {
    private static final AtomicLong IDENTIFIER = new AtomicLong(1L);

    public static void main(String[] args) throws IOException {

        /*if (args.length != 2) {
            System.err.println("Usage: java KKMultiServer <port number> <name>");
            System.exit(1);
        }*/

        int portNumber = Integer.parseInt(args[0]);
        String serverName = args[1];
        boolean listening = true;

        LocationBroadcast broadcast = new LocationBroadcast(portNumber, serverName);
        broadcast.start();

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new KKMultiServerThread(serverSocket.accept(), getNextIdentifier()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            broadcast.stopBroadcasting();
            System.exit(-1);
        }
    }

    /**
     * Increments the {@link #IDENTIFIER} used for the client distinction.
     * {@link #IDENTIFIER} value is reset to zero in case maximum {@code long} value is reached.
     *
     * @return {@code long} value
     */
    private static synchronized long getNextIdentifier() {
        long value = IDENTIFIER.getAndIncrement();
        IDENTIFIER.compareAndSet(Long.MAX_VALUE, 0);

        return value;
    }
}
