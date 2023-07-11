package chat;

import java.util.Objects;
import java.util.function.BiConsumer;

public class ClientMessageObserver implements IClientMessageObserver{
    private final BiConsumer<String, String> messageConsumer;

    private ClientMessageObserver(final BiConsumer<String, String> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public static ClientMessageObserver of(final BiConsumer<String, String> messageConsumer) {
        return new ClientMessageObserver(messageConsumer);
    }

    public void attach() {ClientMessageObservable.getInstance().addObserver(this);}

    public void detach() {ClientMessageObservable.getInstance().removeObserver(this);}

    @Override
    public void onMessageReceived(String clientID, String message) {
        if(Objects.nonNull(clientID) && Objects.nonNull(message))
            messageConsumer.accept(clientID,message);
    }
}
