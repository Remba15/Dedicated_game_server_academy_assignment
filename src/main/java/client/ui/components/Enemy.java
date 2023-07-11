package client.ui.components;

import client.ui.structure.Vector2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Enemy {
    private final String clientID;
    private final List<Vector2<Float>> vertices;

    private Enemy(final String clientID, final List<Vector2<Float>> vertices) {
        this.clientID = clientID;
        this.vertices = vertices;
    }

    public static Enemy of(final String clientID, final String[] vertices) {
        return new Enemy(clientID,
                Arrays.stream(vertices).map(vertex -> {
                    String[] vertexCoordinates = vertex.split(",", 2);
                    return Vector2.of(vertexCoordinates[0], vertexCoordinates[1]);
                }).collect(Collectors.toList()));
    }

    public String getClientID() {
        return clientID;
    }

    public List<Vector2<Float>> getLocation() {
        return vertices;
    }
}
