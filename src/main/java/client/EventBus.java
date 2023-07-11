package client;

import java.util.HashSet;
import java.util.Set;

public class EventBus {
    private static final EventBus INSTANCE = new EventBus();
    private final Set<Subscriber> subscribers = new HashSet<>();

    private EventBus() { }

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public void addObserver(final Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void removeObserver(final Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public synchronized void sendEvent(final Event event, final EventMessage eventMessage) {
        subscribers.forEach(subscriber -> subscriber.onChangeEvent(event, eventMessage));
    }

    public synchronized void sendAvatarMoveEvent(final EventMessage eventMessage) {
        sendEvent(Event.MOVED_AVATAR, eventMessage);
    }

    public synchronized void sendEnemyMoveEvent(final EventMessage eventMessage) {
        if (eventMessage.containsEnemyData())
            sendEvent(Event.MOVED_ENEMY, eventMessage);
    }

    public enum Event {
        MOVED_AVATAR,
        MOVED_ENEMY;

        public boolean isAvatarOnMove() { return this == MOVED_AVATAR; }
        public boolean isEnemyOnMove() { return this == MOVED_ENEMY; }
    }

    public static interface Subscriber {
        void onChangeEvent(Event event, EventMessage eventMessage);
    }
}
