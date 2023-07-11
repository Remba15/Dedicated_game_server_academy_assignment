package client;

import java.util.function.Consumer;

public abstract class MoveEventSubscriber implements EventBus.Subscriber{
    protected final Consumer<EventMessage> onMoveEventCallback;

    protected MoveEventSubscriber(final Consumer<EventMessage> onMoveEventCallback) {
        this.onMoveEventCallback = onMoveEventCallback;
    }

    public void attach() { EventBus.getInstance().addObserver(this); }

    public void detach() { EventBus.getInstance().removeObserver(this); }
}
