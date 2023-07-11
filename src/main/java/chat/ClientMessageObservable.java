package chat;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClientMessageObservable {
    private static final ClientMessageObservable INSTANCE = new ClientMessageObservable();
    private final Set<IClientMessageObserver> observers = new HashSet<>();

    private ClientMessageObservable() { }

    public static ClientMessageObservable getInstance() {return INSTANCE;}

    public void addObserver(final IClientMessageObserver observer) {observers.add(observer);}

    public void removeObserver(final IClientMessageObserver observer) {observers.remove(observer);}

    public synchronized void broadcastMessageFrom(final String clientIdentifier, final String message){
        if (nonNullAndNonEmpty(clientIdentifier) && nonNullAndNonEmpty(message))
            observers.forEach(observer -> observer.onMessageReceived(clientIdentifier, message));
    }

    public static boolean nonNullAndNonEmpty(final String value) {
        return !Objects.isNull(value) && !value.isEmpty();
    }
}
