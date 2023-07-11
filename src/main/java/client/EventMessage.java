package client;

import client.ui.components.Avatar;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import client.ui.components.Enemy;
import client.ui.structure.Vector2;

public class EventMessage {
    private static final Pattern ENEMY_LOCATION =
            Pattern.compile("^.*!(?:-?\\d\\.\\d,-?\\d\\.\\d\\?){3}-?\\d\\.\\d,-?\\d\\.\\d$");

    private final String encodedMessage;

    private EventMessage(final String encodedMessage) {this.encodedMessage = encodedMessage;}

    public static EventMessage of(final Avatar avatar) {
        return new EventMessage(avatar.getVertices().stream().map(Vector2::toString).collect(Collectors.joining("?")));
    }

    public static EventMessage of(final String encodedMessage) {
        return new EventMessage(encodedMessage);
    }

    public String encode() {
        return encodedMessage;
    }

    public Enemy decode() {
        if (encodedMessage == null || encodedMessage.isEmpty()) return null;

        int endOfID = encodedMessage.indexOf('!');
        String clientID = encodedMessage.substring(0, endOfID);
        String[] vertices = encodedMessage.substring(endOfID + 1).split("\\?", 4);

        return Enemy.of(clientID, vertices);
    }

    public boolean containsEnemyData() {
        return ENEMY_LOCATION.matcher(encodedMessage).matches();
    }
}
