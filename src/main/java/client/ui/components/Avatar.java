package client.ui.components;

import client.EventBus;
import client.EventMessage;
import client.ui.structure.Vector2;
import client.ui.structure.Vector3;

public class Avatar extends Quad{
    public Avatar(final Vector2<Integer> windowResolution, final Vector2<Float> sizeToWindowRation,
                  final float yOffset) {
        super(windowResolution, sizeToWindowRation, yOffset);
        color = Vector3.of(1.0f, 0.0f, 0.0f);
        EventBus.getInstance().sendAvatarMoveEvent(EventMessage.of(this)); // send to server initial location
    }

    @Override
    protected void move(final Vector2<Float> direction) {
        super.move(direction);
        EventBus.getInstance().sendAvatarMoveEvent(EventMessage.of(this));
    }
}
