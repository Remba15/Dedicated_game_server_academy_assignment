package client;

import client.ui.components.ServerLocationButton;
import client.ui.structure.Vector2;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ServersOverviewThread extends Thread{
    private static final Vector2<Integer> RESOLUTION = Vector2.of(640, 480);

    private final Set<ServerLocation> serverLocations;
    private final Consumer<ServerLocation> serverSelectionCallback;
    private final List<ServerLocationButton> serverSelectionButtons = new ArrayList<>();
    private long window;

    public ServersOverviewThread(final Set<ServerLocation> serverLocations, Consumer<ServerLocation> serverSelectionCallback) {
        super("WindowThread");
        this.serverLocations = serverLocations;
        this.serverSelectionCallback = serverSelectionCallback;
    }

    @Override
    public void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(RESOLUTION.getX(), RESOLUTION.getY(), "Server List!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");


        // Callback for left mouse button click when choosing the server
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                double[] mouseX = new double[1];
                double[] mouseY = new double[1];
                glfwGetCursorPos(window, mouseX, mouseY);
                Vector2<Integer> clickCoordinates = Vector2.of((int) mouseX[0], (int) mouseY[0]);
                serverSelectionButtons
                        .stream()
                        .filter(serverSelectionButton ->
                                serverSelectionButton.getButton().checkForCollisionWith(clickCoordinates))
                        .findFirst()
                        .map(ServerLocationButton::getServerLocation)
                        .ifPresent(serverLocation -> {
                            glfwSetWindowShouldClose(window, true);
                            serverSelectionCallback.accept(serverLocation);
                        });
            }
        });

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert vidmode != null;
            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // display server selection buttons
            constructServerSelectionButtons();
            displayServerSelectionButtons();

            glfwSwapBuffers(window);
        }
    }

    private void constructServerSelectionButtons() {
        // TODO: remove servers which stopped being available
        // TODO: optimize
        synchronized (serverLocations) {
            serverLocations
                    .stream()
                    .filter(serverLocation -> serverSelectionButtons
                            .stream()
                            .map(ServerLocationButton::getServerLocation)
                            .noneMatch(buttonedServerLocation -> buttonedServerLocation.equals(serverLocation)))
                    .forEach(serverLocation -> {
                        int i = serverSelectionButtons.size();
                        serverSelectionButtons.add(new ServerLocationButton(RESOLUTION, i * 0.15f, serverLocation));
                    });
        }
    }

    private void displayServerSelectionButtons() {
        serverSelectionButtons.forEach(serverLocationButton -> serverLocationButton.getButton().draw());
    }
}
