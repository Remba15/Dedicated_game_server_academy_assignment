package client;

import java.util.Objects;
import java.util.function.Consumer;

public class AvatarMoveEventSubscriber extends MoveEventSubscriber{
    private AvatarMoveEventSubscriber(final Consumer<EventMessage> onMoveEventCallback) {
        super(onMoveEventCallback);
    }

    public static AvatarMoveEventSubscriber of(final Consumer<EventMessage> onMoveEventCallback) {
        return new AvatarMoveEventSubscriber(onMoveEventCallback);
    }

    @Override
    public void onChangeEvent(final EventBus.Event event, final EventMessage eventMessage) {
        if (event.isAvatarOnMove() && Objects.nonNull(eventMessage))
            onMoveEventCallback.accept(eventMessage);
    }
}
