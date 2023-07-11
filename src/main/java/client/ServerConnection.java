package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection extends Thread{
    private final ServerLocation serverLocation;

    public ServerConnection(final ServerLocation serverLocation) {
        super("ServerConnection");
        this.serverLocation = serverLocation;
    }

    @Override
    public void run() {
        System.out.println("Connecting to: " + serverLocation);
        try (Socket kkSocket = new Socket(serverLocation.getAddress(), serverLocation.getPortNumber());
             PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));) {

            String serverMessage;

            Client.InputCollector inputCollector = new Client.InputCollector(out::println);
            inputCollector.start();

            MoveEventSubscriber avatarMoveEventSubscriber =
                    AvatarMoveEventSubscriber.of(msg -> out.println(msg.encode()));
            avatarMoveEventSubscriber.attach();

            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
                if (serverMessage.equals("Bye"))
                    break;
                EventBus.getInstance().sendEnemyMoveEvent(EventMessage.of(serverMessage));
            }

            avatarMoveEventSubscriber.detach();
            inputCollector.stopCollecting();

        } catch (IOException e) {
            System.err.println("Unexpected exception occurred: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
