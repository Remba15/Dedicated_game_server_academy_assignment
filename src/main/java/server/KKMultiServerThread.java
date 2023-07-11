package server;

import chat.ClientMessageObservable;
import chat.ClientMessageObserver;
import knock.KnockKnockProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KKMultiServerThread extends Thread {
    private final Socket socket;
    private final String identifier;

    public KKMultiServerThread(Socket socket, long identifier) {
        super("KKMultiServerThread");
        this.socket = socket;
        this.identifier = "client_" + identifier;
    }

    public void run() {

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));) {

            ClientMessageObserver messageObserver = ClientMessageObserver.of((clientID, message) -> {
                if (!identifier.equals(clientID))
                    out.println(clientID + "!" + message);
            });
            messageObserver.attach();

            // send to client last received messages from the other clients
            Executors.newSingleThreadScheduledExecutor().schedule(
                    () -> LastKnownPlayerLocations.getInstance().getKnownLocations(identifier).forEach(out::println),
                    1000L, TimeUnit.MILLISECONDS);

            String inputLine;
            String outputLine;

            KnockKnockProtocol kkp = new KnockKnockProtocol();
            outputLine = kkp.processInput(null);
            out.println(String.format("Hello %s!", identifier));

            while ((inputLine = in.readLine()) != null) {
                outputLine = kkp.processInput(inputLine);
                if (outputLine.equals("Bye")) {
                    out.println(outputLine);
                    break;
                }

                ClientMessageObservable.getInstance().broadcastMessageFrom(identifier, inputLine);
            }

            messageObserver.detach();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: close socket and detach observer
        }
    }
}
