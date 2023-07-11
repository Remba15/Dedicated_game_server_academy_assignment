package server;

import chat.ClientMessageObservable;
import chat.IClientMessageObserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LastKnownPlayerLocations implements IClientMessageObserver {
    private static final LastKnownPlayerLocations INSTANCE = new LastKnownPlayerLocations();
    private final Map<String, String> locations = new HashMap<>();

    private LastKnownPlayerLocations() { ClientMessageObservable.getInstance().addObserver(this); }

    public static LastKnownPlayerLocations getInstance() { return INSTANCE; }

    @Override
    public void onMessageReceived(final String clientID, final String message) {
        locations.put(clientID, clientID + "!" + message);
    }

    public Collection<String> getKnownLocations(String exceptKey) {
        return locations.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals(exceptKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
