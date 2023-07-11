package client.ui.components;

import client.ui.structure.Vector2;
import client.ui.structure.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public class Quad {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final List<Vector2<Float>> vertices = new ArrayList<>();
    protected Vector3<Float> color = Vector3.of(0.0f, 0.0f, 1.0f);

    public Quad(Vector2<Integer> windowResolution, Vector2<Float> sizeToWindowRation, float yOffset) {
        yOffset += 0.1f; // first button should not be drawn on the top of the window
        this.x = (int) (((1f - sizeToWindowRation.getX()) / 2) * windowResolution.getX());
        this.y = (int) ((yOffset / 2) * windowResolution.getY());
        this.width = (int) (windowResolution.getX() * sizeToWindowRation.getX());
        this.height = (int) (windowResolution.getY() * sizeToWindowRation.getY());
        float x1 = -1 * sizeToWindowRation.getX();
        float x2 = sizeToWindowRation.getX();
        float y1 = 1f - yOffset;
        float y2 = y1 - sizeToWindowRation.getY();
        this.vertices.add(Vector2.of(x1, y1));
        this.vertices.add(Vector2.of(x2, y1));
        this.vertices.add(Vector2.of(x2, y2));
        this.vertices.add(Vector2.of(x1, y2));
    }

    public boolean checkForCollisionWith(Vector2<Integer> mouseClick) {
        return mouseClick.getX() >= x
                && mouseClick.getX() <= x + width
                && mouseClick.getY() >= y
                && mouseClick.getY() <= y + height;
    }

    public void draw() {
        glBegin(GL_QUADS);
        glColor3f(color.getX(), color.getY(), color.getZ());
        vertices.forEach(vertex -> glVertex2f(vertex.getX(), vertex.getY()));
        glEnd();
    }

    public void moveUp() {
        move(Vector2.of(0f, 0.1f));
    }

    public void moveDown() {
        move(Vector2.of(0f, -0.1f));
    }

    public void moveLeft() {
        move(Vector2.of(-0.1f, 0f));
    }

    public void moveRight() {
        move(Vector2.of(0.1f, 0f));
    }

    protected void move(Vector2<Float> direction) {
        List<Vector2<Float>> newVertices = vertices
                .stream()
                .map(vertex -> Vector2.of(vertex.getX() + direction.getX(), vertex.getY() + direction.getY()))
                .collect(Collectors.toList());

        vertices.clear();
        vertices.addAll(newVertices);
    }

    public List<Vector2<Float>> getVertices() {
        return List.copyOf(vertices);
    }

    public void update(List<Vector2<Float>> location) {
        vertices.clear();
        vertices.addAll(location);
    }
}
