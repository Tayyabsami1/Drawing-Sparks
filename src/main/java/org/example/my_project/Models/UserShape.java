package org.example.my_project.Models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;

/**
 * The {@code UserShape} class represents a user-shaped stick figure that can be drawn on a canvas.
 * It extends the {@code Shape} abstract class and provides specific implementations for width,
 * height, and drawing methods.
 *
 * <p>Each instance of {@code UserShape} represents a stick figure with a label,
 * positioned at a specific (x, y) coordinate.</p>
 *
 * <p>This class implements {@code Serializable} to allow instances to be serialized.</p>
 *
 * @author Abdul Ahad
 * @author Tayyab
 * @author Zeeshan
 * @version 1.0
 * @since 2024-12-05
 */
public class UserShape extends Shape implements Serializable {
    /**
     * A static counter to keep track of the number of {@code UserShape} instances created.
     */
    public static int count = 0;

    /**
     * Constructs a new {@code UserShape} with the specified position and name.
     * Increments the static {@code count} variable to track the number of instances.
     *
     * @param x The x-coordinate of the shape's position.
     * @param y The y-coordinate of the shape's position.
     * @param n The name of the shape.
     */
    public UserShape(double x, double y, String n) {
        super(x, y, n);
        count++;
    }

    /**
     * Returns the width of the {@code UserShape}.
     *
     * @return The width of the stick figure, which is always {@code 30}.
     */
    public double getWidth() {
        return 30;
    }

    /**
     * Returns the height of the {@code UserShape}.
     *
     * @return The height of the stick figure, which is always {@code 70}.
     */
    public double getHeight() {
        return 70;
    }

    /**
     * Draws the stick figure representing the {@code UserShape} on the given {@code GraphicsContext}.
     *
     * <p>The stick figure consists of a circular head, a body, arms, legs, and a label for the name.</p>
     *
     * @param gc The {@code GraphicsContext} used to draw the shape on a canvas.
     */
    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);

        // Draw the stick figure
        double centerX = x + 15;
        gc.strokeOval(centerX - 10, y, 20, 20); // Head
        gc.strokeLine(centerX, y + 20, centerX, y + 50); // Body
        gc.strokeLine(centerX, y + 50, centerX - 15, y + 70); // Left leg
        gc.strokeLine(centerX, y + 50, centerX + 15, y + 70); // Right leg
        gc.strokeLine(centerX, y + 30, centerX - 15, y + 20); // Left arm
        gc.strokeLine(centerX, y + 30, centerX + 15, y + 20); // Right arm

        // Draw the label
        gc.setFill(Color.BLACK);
        gc.fillText(name, centerX - 20, y + 85);
    }
}
