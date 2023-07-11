package client.ui.components;

import client.ServerLocation;
import client.ui.structure.Vector2;

public class ServerLocationButton {
    private static final Vector2<Float> SIZE_TO_WINDOW_RATION = Vector2.of(0.6f, 0.1f);

    private final Quad button;
    private final ServerLocation serverLocation;

    public ServerLocationButton(Vector2<Integer> windowResolution, float yOffset, ServerLocation serverLocation) {
        this.button = new Quad(windowResolution, SIZE_TO_WINDOW_RATION, yOffset);
        this.serverLocation = serverLocation;
    }

    public Quad getButton() { return button; }

    public ServerLocation getServerLocation() {
        return serverLocation;
    }
}
