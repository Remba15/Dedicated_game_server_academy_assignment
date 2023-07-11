package client;


import client.ui.components.Avatar;
import client.ui.components.Enemy;
import client.ui.components.Quad;
import client.ui.structure.Vector2;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Game {
    private static final Vector2<Integer> RESOLUTION = Vector2.of(640, 480);
    private final Avatar avatar = new Avatar(RESOLUTION, Vector2.of(0.2f, 0.2f), 0.8f);
    private final Map<String, Quad> enemies = new HashMap<>();
    private long window;

    public void run() {
        MoveEventSubscriber enemyMoveEventSubscriber = EnemyMoveEventSubscriber.of(msg -> {
            Enemy enemy = msg.decode();
            enemies.putIfAbsent(enemy.getClientID(), new Quad(RESOLUTION, Vector2.of(0.2f, 0.2f), 0.8f));
            enemies.get(enemy.getClientID()).update(enemy.getLocation());
        });
        enemyMoveEventSubscriber.attach();

        init();
        loop();

        enemyMoveEventSubscriber.detach();

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
        window = glfwCreateWindow(RESOLUTION.getX(), RESOLUTION.getY(), "GAME!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            switch (key) {
                case GLFW_KEY_W:
                    avatar.moveUp();
                    break;
                case GLFW_KEY_S:
                    avatar.moveDown();
                    break;
                case GLFW_KEY_A:
                    avatar.moveLeft();
                    break;
                case GLFW_KEY_D:
                    avatar.moveRight();
                    break;
                default:
            }

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
            enemies.values().forEach(Quad::draw);
            avatar.draw();
            glfwSwapBuffers(window);
        }
        System.out.println("Game finished!");
    }
}
