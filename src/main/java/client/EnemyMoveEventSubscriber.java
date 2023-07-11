package client;

import java.util.Objects;
import java.util.function.Consumer;

public class EnemyMoveEventSubscriber extends MoveEventSubscriber{
    private EnemyMoveEventSubscriber(final Consumer<EventMessage> onMoveEventCallback) {
        super(onMoveEventCallback);
    }

    public static EnemyMoveEventSubscriber of(final Consumer<EventMessage> onMoveEventCallback) {
        return new EnemyMoveEventSubscriber(onMoveEventCallback);
    }

    @Override
    public void onChangeEvent(final EventBus.Event event, final EventMessage eventMessage) {
        if (event.isEnemyOnMove() && Objects.nonNull(eventMessage))
            onMoveEventCallback.accept(eventMessage);
    }
}
